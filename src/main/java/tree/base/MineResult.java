//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tree.base;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class MineResult {
    private int uid;
    private Map<Integer, Double> uidWeightMap = new LinkedHashMap<>();
    public MineResult(int uid) {
        this.uid = uid;
    }
    public void add(int uid, double weight) {
        if (!this.uidWeightMap.containsKey(uid)) {
            this.uidWeightMap.put(uid, weight);
        }
    }
    public int getUid() {
        return this.uid;
    }

    public int size() {
        return this.uidWeightMap.size();
    }

    public List<Double> getWeightList() {
        return new ArrayList<>(this.uidWeightMap.values());
    }

    public List<Integer> getUidList() {
        return new ArrayList<>(this.uidWeightMap.keySet());
    }

    @Override
    public String toString() {
        if (this.uidWeightMap.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(1 + this.uidWeightMap.size() * 10);
            sb.append(this.uid).append(' ');
            for (Map.Entry<Integer, Double> entry : this.uidWeightMap.entrySet()) {
                sb.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue())
                        .append(' ');
            }
            sb.append('\n');
            return sb.toString();
        }
    }
}
