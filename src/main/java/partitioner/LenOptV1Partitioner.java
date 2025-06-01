//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package partitioner;
import java.util.Iterator;
import java.util.Map;

public class LenOptV1Partitioner extends LenOptiPartitioner {
    public LenOptV1Partitioner(int numPartition, double threshold) {
        super(numPartition, threshold);
    }

    public boolean updateState(String line) {
        int len = this.countSpace(line);
        this.maxLen = Integer.max(len, this.maxLen);
        this.minLen = Integer.min(len, this.minLen);
        this.len2count.put(len, this.len2count.getOrDefault(len, 0.0) + 1.0);//长度 对应个数
        return true;
    }

    public void prepareLoad() {
        int sz = this.maxLen - this.minLen + 1;//集合长度
        System.out.println("sz = " + sz);
        this.loadSum = new double[sz + 1];
        this.countSum = new double[sz + 1];
        Map.Entry entry;
        for(Iterator var2 = this.len2count.entrySet().iterator(); var2.hasNext(); this.countSum[(Integer)entry.getKey() - this.minLen + 1] = (Double)entry.getValue()) {//遍历len2count
            entry = (Map.Entry)var2.next();//len2count的组成为(int,double)
            this.loadSum[(Integer)entry.getKey() - this.minLen + 1] = (Double)entry.getValue() * (double)(Integer)entry.getKey();//个数*长度
        }

        for(int i = 0; i < sz; ++i) {
            this.loadSum[i + 1] += this.loadSum[i];
            this.countSum[i + 1] += this.countSum[i];
        }
    }

    //计算 load 函数
    public double getLoad(int l1, int l2) {
        int l1i = l1;

        int l2i;
        for(l2i = l2; l1i <= l2i && this.countSum[l1i - this.minLen] == this.countSum[l1i + 1 - this.minLen]; ++l1i) {
        }

        while(l1i <= l2i && this.countSum[l2i - this.minLen] == this.countSum[l2i + 1 - this.minLen]) {
            --l2i;
        }

        int l1p = Math.max(this.minLen, (int)Math.ceil((double)l1i * this.threshold));
        int l2n = Math.min(this.maxLen, (int)Math.floor((double)l2i / this.threshold));
        double RelemSum = this.load(l1, l2);
        double SelemSum = this.load(l1p, l2n);
        return SelemSum * RelemSum + RelemSum;
    }
}

