//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package partitioner;

public abstract class PartitionerBase {
    private int numPartition;

    public PartitionerBase(int numPartition) {
        this.numPartition = numPartition;
    }

    public int getNumPartition() {
        return this.numPartition;
    }

    public void setNumPartition(int numPartition) {
        this.numPartition = numPartition;
    }

    public abstract boolean updateState(String var1);

    public abstract void initPartition();

    public abstract int[] getRIndex(String var1);

    public abstract int[] getSIndex(String var1);

    public abstract String serialize();

    public abstract PartitionerBase deserialize(String var1);

    public static PartitionerBase getPartitioner(String methodName, int numPartition, double threshold) {
        if (methodName.equals("hash")) {
            return new HashPartitioner(numPartition);
        }
        else if (methodName.equals("lenv1")) {
            return new LenOptV1Partitioner(numPartition, threshold);
        }
        return new HashPartitioner(numPartition);
    }

    public String toString() {
        return this.serialize();
    }
}

