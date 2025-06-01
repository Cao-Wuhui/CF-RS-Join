//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.base;

import java.util.*;
import tree.fpstree.FPSTree;
import utils.Pair;
import utils.Record;
public abstract class TreeBase {
    public TreeBase() {
    }
    public abstract void createTree(Object var1, double threshold, List<Record> listofS) throws InterruptedException;
    public abstract List<Query> generateQuery(List<Record> lisofR);
    public abstract MineResult search(Query record);
    public void mineParallel(List<Query> queryList, List<RunnerBase> runners) throws InterruptedException {
        int i = 0;
        int numThread = runners.size();

        Iterator var5;
        for (var5 = queryList.iterator(); var5.hasNext(); i = (i + 1) % numThread) {
            Query query = (Query) var5.next();
            (runners.get(i)).add(query); //每个线程runner所对应的query
        }
        var5 = runners.iterator();
        RunnerBase runner;
        //开启每个线程
        while (var5.hasNext()) {
            runner = (RunnerBase) var5.next();
            runner.start();
        }
        var5 = runners.iterator();
        while (var5.hasNext()) {
            runner = (RunnerBase) var5.next();
            runner.join();
        }
    }


    public void parseAidToPairListInReader(List<Record> listofS, LinkedHashMap<Integer, List<Pair>> aidToPairList){
        Iterator list = listofS.iterator();
        while (list.hasNext()) {
            Record record = (Record) list.next();
            int sid = record.recordid;
            int slen = record.tokens.size();
            Pair pair = new Pair(sid, slen);
            for (int i = 0; i < slen; i++) {//遍历元素
                int token = record.tokens.get(i);
                List<Pair> pairs = aidToPairList.get(token);
                if (pairs == null) {
                    pairs = new ArrayList<>();
                    aidToPairList.put(token, pairs);
                }
                pairs.add(pair);
            }
        }
    }

    //获取树类型
    public static TreeBase getTree(String treeType) {
        if (treeType == null) {
            return null;
        } else if (treeType.equals("fpstree")) {
            return new FPSTree();
        }
        return null;
    }
}
