package run;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import tree.base.Query;
import tree.base.RunnerBase;
import tree.base.TreeBase;
import tree.fpstree.FPSTree;
import utils.Pair;
import utils.Record;

public class RunSingle {
    public RunSingle() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        String treeType = args[0];
//        String rInputPath = args[1];
//        String sInputPath = args[2];
//        String outputPath = args[3];
//        double threshold = Double.parseDouble(args[4]);
//        int numThread = Integer.parseInt(args[5]);
//        String logpath = args[6];
        String treeType = "fpstree";
        String rInputPath = "src/main/java/input/r_1w_dec.txt";//rinput建树
        String sInputPath = "src/main/java/input/s_1w_dec.txt";//sinput搜素
        String outputPath = "src/main/java/output/out1";
        double threshold = (double)(0.9);
        int numThread = 1;
        String logpath = "src/main/java/output/lo";

        System.out.println("rInputPath = " + rInputPath);
        System.out.println("sInputPath = " + sInputPath);
        System.out.println("outputPath = " + outputPath);
        System.out.println("threshold = " + threshold);
        System.out.println("numThread = " + numThread);

        List<Record> listofS = new ArrayList<>(changetorecord(sInputPath));
        List<Record> listofR = new ArrayList<>(changetorecord(rInputPath)); //处理为 List<Record>
        //s需要转置处理，r不需要
        //读r跟s，动态规划计算分区
        //得到分区计划

        Date s = new Date();
        TreeBase tree = TreeBase.getTree(treeType);
        LinkedHashMap<Integer, List<Pair>> aidToPairList = countAndReverse(tree, listofS); //因为先从第0行开始遍历，所以
        Date r = new Date();
        double indeTime= ((r.getTime() - s.getTime())*1.0 / 1000L);
        System.out.println("reverse Time is " + indeTime + " second");

        FileOutputStream f2 = new FileOutputStream(outputPath);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(f2);
        BufferedWriter writer = new BufferedWriter(outputStreamWriter);

        Date startTime = new Date();
        Date startIndexTime = new Date();

        tree.createTree(aidToPairList, threshold, listofS);
        Date endIndexTime = new Date();
        double indexTime = ((endIndexTime.getTime() - startIndexTime.getTime())*1.0 / 1000L);
        System.out.println("Index Time is " + indexTime + " second");
        Date startMineTime = new Date();
//        tree.info(); 获取树中结点的个数
        mineLocal(tree, listofR, numThread, writer);
        Date endMineTime = new Date();
        double mineTime = ((endMineTime.getTime() - startMineTime.getTime())*1.0 / 1000L);
        System.out.println("Mine Time is " + mineTime + " second");
        Date endTime = new Date();
        double allTime = ((endTime.getTime() - startTime.getTime())*1.0 / 1000L);
        System.out.println("Time is " + allTime + " second");

        FileOutputStream out2 = new FileOutputStream(logpath, true);
        PrintWriter writer2 = new PrintWriter(out2);

        writer2.write("************(L)FV-treeR-SJ************\n");
        writer2.write("treeType: " + treeType + "\n");
        writer2.write("rInput: " + rInputPath + "\n");
        writer2.write("sInput: " + sInputPath + "\n");
        writer2.write("output: " + outputPath + "\n");
        writer2.write("threshold: " + threshold + "\n");
        writer2.write("indexTime: " + indexTime + "\n");
        writer2.write("mineTime: " + mineTime + "\n");
        writer2.write("time: " + allTime + "\n");
        writer2.close();
    }
    //统一输入格式
    public static List<Record> changetorecord(String inputPath) throws IOException {
        FileInputStream f = new FileInputStream(inputPath);
        InputStreamReader inputStreamReader = new InputStreamReader(f);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        List<Record> soC = new ArrayList<>();
        parseAidToglobalsorting(reader, soC);
        return soC;
    }

    public static void parseAidToglobalsorting(BufferedReader reader, List<Record> soC) throws IOException {
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            Record record = new Record(i);
            String rid = st.nextToken();
            while (st.hasMoreTokens()) {
                String aid = st.nextToken();
                record.tokens.add(Integer.parseInt(aid));
            }
            soC.add(record);
            i++;
        }
    }
    //计算数据集特征
    public static void info_aidtopairlist(long A, LinkedHashMap<Integer, List<Pair>> aidToPairList)
    {
        //A为总元素个数
        long maxseq=0;//最大长度
        int avgseq=0;//平均长度
        long total = 0;
        System.out.println("A = ?"+aidToPairList.size());
        ArrayList<Map.Entry<String, List<Pair>>> entryList = new ArrayList(aidToPairList.entrySet());
        Iterator seq = entryList.iterator();
        while(seq.hasNext())//遍历每个seq
        {
            Map.Entry<String, List<Pair>> p = (Map.Entry)seq.next();
            int size=p.getValue().size();//该行seq的值大小
            avgseq=avgseq+size;
            if(size>maxseq)
                maxseq=size;
        }
        System.out.println("max seq is " + maxseq);
        System.out.println("Total set in seq(set) has : " + avgseq);
        System.out.println("avg seq is " + avgseq*1.0/aidToPairList.size());
    }
    //转置数据集
    public static LinkedHashMap<Integer, List<Pair>> countAndReverse(TreeBase tree, List<Record> listofS) throws IOException {
        LinkedHashMap<Integer, List<Pair>> aidToPairList = new LinkedHashMap<>();
        tree.parseAidToPairListInReader(listofS, aidToPairList);
        return aidToPairList;
    }
    //搜索函数入口
    public static void mineLocal(TreeBase tree, List<Record> lisofR, int numThread, BufferedWriter writer) throws IOException, InterruptedException {
        Date startIndexTime = new Date();
        List<Query> queryList = tree.generateQuery(lisofR);
        Date endIndexTime = new Date();
        double indexTime = ((endIndexTime.getTime() - startIndexTime.getTime())*1.0 / 1000L);
        System.out.println("Generate query Time is " + indexTime + " second");

        if (numThread > queryList.size()) {
            numThread = queryList.size();
        }
        List<RunnerBase> runners = new ArrayList();
        for(int i = 0; i < numThread; ++i) {
            runners.add(new SubRunner(tree, writer));
        }
        tree.mineParallel(queryList, runners);
        writer.close();
    }
}
