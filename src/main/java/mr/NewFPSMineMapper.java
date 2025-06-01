//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import partitioner.PartitionerBase;
import utils.Parameters;

import java.io.IOException;

public class NewFPSMineMapper extends Mapper<Object, Text, IntWritable, Text> {
    private IntWritable outputKey = new IntWritable();
    private Text outputValue = new Text();
    private SetType type;
    private PartitionerBase partitioner;

    public NewFPSMineMapper() {
    }

    protected void map(Object key, Text value, Mapper<Object, Text, IntWritable, Text>.Context context) throws IOException, InterruptedException {
        int[] partitions;//负责把s和r分到不同key值里去
        if (this.type == SetType.R_SET) {
            partitions = this.partitioner.getRIndex(value.toString());//获取相应的集合索引位置（reducer编号）
        } else {
            partitions = this.partitioner.getSIndex(value.toString());
        }

        this.outputValue.set(value);
        int[] var5 = partitions;
        int partitionlength = partitions.length;

        for(int var7 = 0; var7 < partitionlength; ++var7) {
            int index = var5[var7];
            if (index == -1) {
                break;
            }

            this.outputKey.set(index);
            context.write(this.outputKey, this.outputValue);
        }

    }

    protected void setup(Mapper<Object, Text, IntWritable, Text>.Context context) throws IOException, InterruptedException {//每个mapTask执行一次
        String fileName = ((FileSplit)context.getInputSplit()).getPath().getName();
        System.out.println(fileName);
        //对集合进行分类
        if (fileName.charAt(0) == 'r') {
            this.type = SetType.S_SET;
        } else {
            this.type = SetType.R_SET;
        }

        Parameters parameters = new Parameters(context.getConfiguration().get("fpsjoin.paramters", ""));
        int numPartition = parameters.getInt("numReducer", 16);
        double threshold = parameters.getDouble("threshold", 1.0);
        String method = parameters.get("partition_method", "random");
        this.partitioner = PartitionerBase.getPartitioner(method, numPartition, threshold);
        this.partitioner.deserialize(parameters.get("parition"));//解析划分方式的字符春
    }
}
