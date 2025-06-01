//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package partitioner;

import java.util.StringTokenizer;
import java.util.TreeMap;

public abstract class LenOptiPartitioner extends PartitionerBase {
    protected double threshold;
    protected TreeMap<Integer, Double> len2count = new TreeMap();
    protected int[] partitions;
    protected int maxLen = 0;
    protected int minLen = Integer.MAX_VALUE;
    protected double maxLoad;
    private int[] Rres = new int[1];
    protected double[] loadSum;
    protected double[] countSum;

    public LenOptiPartitioner(int numPartition, double threshold) {
        super(numPartition);
        this.threshold = threshold;
        this.partitions = new int[numPartition];
    }

    public int countSpace(String s) {
        int c = 0;

        for(int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ' ' || s.charAt(i) == '\t') {
                ++c;
            }
        }

        return c;
    }

    public boolean updateState(String line) {
        int len = this.countSpace(line);//空格数量=元素个数
        this.maxLen = Integer.max(len, this.maxLen);//记录最长集合和最短集合的长度
        this.minLen = Integer.min(len, this.minLen);
        this.len2count.put(len, (Double)this.len2count.getOrDefault(len, 0.0) + 1.0);//记录 某长度的 集合数量
        return true;
    }

    protected double load(int l1, int l2) {
        return this.loadSum[l2 + 1 - this.minLen] - this.loadSum[l1 - this.minLen];
    }

    public abstract void prepareLoad();

    public abstract double getLoad(int var1, int var2);

    public void initPartition(){
        this.maxLoad = 0.0;
        int sz = this.maxLen - this.minLen + 1;//总区间长度
        this.prepareLoad();//获得loadSum countSum
        int i, n, r, l;
        double temp;

        double[] prefixSum = new double[maxLen - minLen + 2];  // 前缀和数组
        double[] prev = new double[sz];  // 上一层的状态
        double[] curr = new double[sz];  // 当前层的状态
        int[][] split = new int[this.getNumPartition() + 1][sz + 1];  // split[n][r] 记录分割点 l

        prefixSum[0] = 0.0;
        for (i = minLen; i <= maxLen; i++) {
            prefixSum[i - minLen + 1] = prefixSum[i - minLen] + getLoad(i, i);
        }


        // 初始化第一层
        for (i = 0; i < sz; i++) {
            prev[i] = this.getLoad(this.minLen, i + this.minLen);
        }

        // 计算后续层
        for (n = 2; n <= this.getNumPartition(); ++n) {
            for (r = n + this.minLen - 1; r <= this.maxLen; ++r) {
                double minMax  = Double.MAX_VALUE;
                for (l = n + this.minLen - 2; l < r; ++l) {
                    temp = Math.max(prev[l - this.minLen], prefixSum[r - this.minLen + 1] - prefixSum[l + 1 - this.minLen]);
                    if (temp < minMax) {
                        minMax  = temp;
                        split[n][r - this.minLen] = l;  // 记录分割点
                    }
                }
                curr[r - this.minLen] = minMax ;
            }
            prev = curr.clone();
        }

        n = this.getNumPartition();
        r = this.maxLen;
        l = split[n][r - this.minLen];
        while (n > 1 && l > 0) {
            System.out.println("n = " + n + " l1 = " + (l + 1) + " l2 = " + r + " load = " + (prefixSum[r - this.minLen + 1] - prefixSum[l - this.minLen + 1]));
            --n;
            this.partitions[n - 1] = l;
            r = l;
            l = split[n][r - this.minLen];
        }
        System.out.println("n = " + n + " l1 = " + this.minLen + " l2 = " + r + " load = " + (prefixSum[r - this.minLen + 1] - prefixSum[0]));
        this.partitions[this.getNumPartition() - 1] = Integer.MAX_VALUE;
    }

    private int getIndex(int len) {
        int index;
        for(index = 0; len > this.partitions[index]; ++index) {

        }

        return index;
    }

    public int[] getRIndex(String line) {
        int len = this.countSpace(line);
        this.Rres[0] = this.getIndex(len);
        return this.Rres;
    }

    public int[] getSIndex(String line) {
        int len = this.countSpace(line);
        int lowerLen = (int)Math.ceil((double)len * this.threshold);
        int upperLen = (int)Math.floor((double)len / this.threshold);//相似的上界和下界
        int lowerIndex = this.getIndex(lowerLen);//上界和下界的index
        int upperIndex = this.getIndex(upperLen);
        int[] res = new int[upperIndex - lowerIndex + 1];

        for(int i = 0; i < res.length; ++i) {
            res[i] = lowerIndex++;
        }

        return res;
    }

    public String serialize() {
        StringBuilder st = new StringBuilder();

        for(int i = 0; i < this.partitions.length - 1; ++i) {
            st.append(this.partitions[i]).append(' ');
        }

        st.append(this.partitions[this.partitions.length - 1]);
        return st.toString();
    }

    public PartitionerBase deserialize(String string) {
        StringTokenizer st = new StringTokenizer(string);

        for(int i = 0; st.hasMoreTokens(); this.partitions[i++] = Integer.parseInt(st.nextToken())) {
        }

        return this;
    }
}
