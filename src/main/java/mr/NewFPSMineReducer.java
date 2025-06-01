//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mr;

import com.sun.corba.se.spi.legacy.interceptor.IORInfoExt;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import tree.base.MineResult;
import tree.base.Query;
import tree.base.RunnerBase;
import tree.base.TreeBase;
import utils.Pair;
import utils.Parameters;
import utils.Record;
import utils.Timer;

import java.io.IOException;
//import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class NewFPSMineReducer extends Reducer<IntWritable, Text, Text, Text> {
    private double threshold;
    private TreeBase tree;
    private int numThread;
    private Timer timer = new Timer("Reduce");
    private long numset = 0;
    private long numele = 0;

    public NewFPSMineReducer() {
    }


    protected void reduce(IntWritable key, Iterable<Text> values, Reducer<IntWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        List<Record> listofR = new ArrayList();
        List<Record> listofS = new ArrayList<>();
        int maxSLen = Integer.MIN_VALUE, minSLen = Integer.MAX_VALUE;
        Set<Integer> numSLen = new HashSet<>();
        Iterator mapvalue = values.iterator();//map传来的value(0/1/?, <ri,Ri>) R建树 S搜索
        String u;

        while(mapvalue.hasNext()) {
            Text text = (Text)mapvalue.next();
            u = text.toString();

            StringTokenizer st = new StringTokenizer(u);
            int uid = Integer.parseInt(st.nextToken().substring(1));
            Record record = new Record(uid);
            while (st.hasMoreTokens()) {
                String aid = st.nextToken();
                record.tokens.add(Integer.parseInt(aid));
            }

            if (u.charAt(0) == 'r') {//如果是r 加进SList 也就是作为搜索集
                listofR.add(record);
            } else {
                int setLen = record.tokens.size();
                listofS.add(0, record);
                maxSLen = Math.max(maxSLen, setLen);
                minSLen = Math.min(minSLen, setLen);
                numSLen.add(setLen);
            }
        }
        //建树
        LinkedHashMap<Integer, List<Pair>> aidToPairList = new LinkedHashMap();
        this.tree.parseAidToPairListInReader(listofS, aidToPairList);
        this.tree.createTree(aidToPairList, this.threshold, listofS);
        //分配查询集
        List<Query> queryList = this.tree.generateQuery(listofR);
        Collections.sort(queryList);
        if (this.numThread > queryList.size()) {
            this.numThread = queryList.size();
        }
        List<RunnerBase> runners = new ArrayList();
        for(int i = 0; i < this.numThread; ++i) {
            runners.add(new SubRunner(context));
        }
        //搜索
        this.tree.mineParallel(queryList, runners);
    }

    protected void setup(Reducer<IntWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        Parameters parameters = new Parameters(context.getConfiguration().get("fpsjoin.paramters", ""));
        this.tree = TreeBase.getTree(parameters.get("treeType"));
        this.threshold = parameters.getDouble("threshold", 1.0);
        this.numThread = parameters.getInt("numThread", 20);
    }//准备用从mapper拉取的输出进行建树

    @Override
    protected void cleanup(Context context) throws IOException{
        Parameters params = new Parameters(context.getConfiguration().get("fpsjoin.paramters",""));
        //将分片计数写入HDFS文件
        String taskId = context.getTaskAttemptID().toString();
        Path outputPath = new Path(params.get("output") + "/counter_output/" + taskId + ".txt");
        try(FSDataOutputStream out = FileSystem.get(context.getConfiguration()).create(outputPath)){
            out.writeUTF("The number of set is "+ numset + " The number of elem is "+ numele + "\n");
        }
    }

    //实现多线程
    class SubRunner extends RunnerBase {
        private final Reducer<IntWritable, Text, Text, Text>.Context context;
        List<Query> queryList = new ArrayList();
        private Text outputKey = new Text();
        private Text outputValue = new Text();
        public SubRunner(Reducer<IntWritable, Text, Text, Text>.Context context) {
            this.context = context;
        }

        public void add(Query query) {
            this.queryList.add(query);
        }

        public void run() {
            for (Query query : this.queryList) {
                MineResult res = NewFPSMineReducer.this.tree.search(query);
                if (res.size() != 0) {
                    List<Double> weightList = res.getWeightList();
                    List<Integer> uidList = res.getUidList();
                    int rid = res.getUid();
                    for (int i = 0; i < weightList.size() && i < uidList.size(); i++) {
                        double weight = weightList.get(i);
                        int sid = uidList.get(i);
                        DecimalFormat df = new DecimalFormat("0.00");
                        this.outputKey.set("r" + rid + " s" + sid + " " + df.format(weight));
                        try {
                            this.context.write(this.outputKey, this.outputValue);
                        } catch (IOException | InterruptedException var7) {
                            var7.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

