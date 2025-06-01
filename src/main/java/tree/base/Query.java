//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.base;

import java.util.ArrayList;
import java.util.List;
import utils.Pair;

public class Query implements Comparable<Query> {
    private Integer uid;
    private int uidSize;
    private List<Pair> aidPairs = new ArrayList();
    public Query(int uid, int uidSize) {
        this.uid = uid;
        this.uidSize = uidSize;
    }
    public int getUid() {
        return this.uid;
    }
    public int getUidSize() {
        return this.uidSize;
    }
    public List<Pair> getAidPairs() {
        return this.aidPairs;
    }
    public void addPair(Pair pair) {
        this.aidPairs.add(pair);
    }
    public int compareTo(Query o) {
        int compare = o.uidSize - this.uidSize;
        return compare == 0 ? this.uid.compareTo(o.uid) : compare;
    }
    public String toString() {
        return "Query{uid='" + this.uid + '\'' + ", uidSize=" + this.uidSize + ", aidPairs=" + this.aidPairs + '}';
    }
}
