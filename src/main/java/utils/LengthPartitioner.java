//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.StringTokenizer;

public class LengthPartitioner {
    public int[] lengths;

    public LengthPartitioner(String str) {
        StringTokenizer st = new StringTokenizer(str);
        this.lengths = new int[st.countTokens()];

        for(int i = 0; st.hasMoreTokens(); ++i) {
            this.lengths[i] = Integer.parseInt(st.nextToken());
        }

    }

    public int getParition(int length) {
        int i;
        for(i = 0; i < this.lengths.length && length > this.lengths[i]; ++i) {
        }
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < this.lengths.length; ++i) {
            stringBuilder.append(this.lengths[i]).append(' ');
        }

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        LengthPartitioner lp = new LengthPartitioner("76 644");
        System.out.println("lp.getParition(3) = " + lp.getParition(3));
        System.out.println("lp.getParition(76) = " + lp.getParition(76));
        System.out.println("lp.getParition(600) = " + lp.getParition(600));
        System.out.println("lp.getParition(645) = " + lp.getParition(645));
    }
}
