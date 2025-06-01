package run;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import tree.base.MineResult;
import tree.base.Query;
import tree.base.RunnerBase;
import tree.base.TreeBase;

public class SubRunner extends RunnerBase {
    List<Query> queryList = new ArrayList();
    TreeBase tree;
    final BufferedWriter writer;
    public SubRunner(TreeBase tree, BufferedWriter writer) {
        this.tree = tree;
        this.writer = writer;
    }
    public void add(Query query) {
        this.queryList.add(query);
    }
    public void run() {
        for (Query query : this.queryList) {
            MineResult res = this.tree.search(query);
            if (res.size() != 0) {
                List<Double> weightList = res.getWeightList();
                List<Integer> uidList = res.getUidList();
                int rid = res.getUid();
                for (int i = 0; i < weightList.size() && i < uidList.size(); i++) {
                    double weight = weightList.get(i);
                    int sid = uidList.get(i);
                    DecimalFormat df = new DecimalFormat("0.00");
                    try {
                    this.writer.write("r" + rid + " s" + sid + " " + df.format((double) weight) + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                }
            }
        }
    }
}
