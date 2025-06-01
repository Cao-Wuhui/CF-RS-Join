//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.Objects;

public class Pair implements Comparable<Pair> {
    private Integer first;
    private Integer second;

    public Pair() {
    }

    public Pair(Integer first, Integer second) {
        this.first = first;
        this.second = second;
    }
    public Integer getFirst() {
        return this.first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getSecond() {
        return this.second;
    }
    public void setSecond(Integer second) {
        this.second = second;
    }

    public int compareTo(Pair o) {
        int compare = o.second.compareTo(this.second);
        return compare == 0 ? this.first.compareTo(o.first) : compare;
    }

    public int hashCode() {
        int firstHash = hashCodeNull(this.first);
        return (firstHash >>> 16 | firstHash << 16) ^ hashCodeNull(this.second);
    }

    private static int hashCodeNull(Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        } else {
            Pair otherPair = (Pair) obj;
            return isEqualOrNulls(this.first, otherPair.getFirst()) && isEqualOrNulls(this.second, otherPair.getSecond());
        }

    }

    private static boolean isEqualOrNulls(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }
}

