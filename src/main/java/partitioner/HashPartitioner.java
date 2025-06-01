//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package partitioner;

public class HashPartitioner extends PartitionerBase {
    private int[] partition;
    private int[] res = new int[1];

    public HashPartitioner(int numPartition) {
        super(numPartition);
    }

    public boolean updateState(String line) {
        return false;
    }

    public void initPartition() {
        this.partition = new int[this.getNumPartition()];

        for(int i = 0; i < this.getNumPartition(); this.partition[i] = i++) {
        }
    }

    public int[] getRIndex(String line) {
        return this.partition;
    }

    public int[] getSIndex(String line) {
        this.res[0] = (line.hashCode() & Integer.MAX_VALUE) % this.getNumPartition();
        return this.res;
    }

    public String serialize() {
        return "";
    }

    public PartitionerBase deserialize(String string) {
        this.initPartition();
        return this;
    }

    public String toString() {
        return "HashPartitioner{}";
    }
}
