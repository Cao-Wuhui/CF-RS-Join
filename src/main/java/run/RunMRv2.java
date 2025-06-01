package run;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import java.io.*;
import java.util.Date;

import mr.NewFPSMineMapper;
import mr.NewFPSMineReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import partitioner.PartitionerBase;
import utils.Parameters;


public class RunMRv2 {
    private static long diskUsage;
    private static long allReadOps;
    private static long allWriteOps;
    private static boolean useDeduplicateJob = false;

    public RunMRv2() {
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();//创建配置文件
        Parameters parameters = new Parameters();
        String treeType = "fpstree";
        String rInputPath = "I:\\集合相似度计算\\CF-RS-Join\\src\\main\\java\\input\\r_1w_dec.txt";
        String sInputPath = "I:\\集合相似度计算\\CF-RS-Join\\src\\main\\java\\input\\s_1w_dec.txt";
        String outputPath = "I:\\集合相似度计算\\CF-RS-Join\\src\\main\\java\\output";
        String partitionMethod = "lenv1";//partition方法
        int numReducer = 1;
        double threshold = (double)(0.7);
        int numThread = 1;
        int numSplitTree = 1;
        String logpath = "I:\\集合相似度计算\\CF-RS-Join\\src\\main\\java\\output\\lo";

        parameters.set("treeType", treeType);
        parameters.set("rInput", rInputPath);
        parameters.set("sInput", sInputPath);
        parameters.set("output", outputPath);
        parameters.set("threshold", Double.toString(threshold));
        parameters.set("numReducer", Integer.toString(numReducer));
        parameters.set("numThread", Integer.toString(numThread));
        parameters.set("numSplitTree", Integer.toString(numSplitTree));

//        parameters.set("threshold", args[5]);
//        parameters.set("numReducer", args[6]);
//        parameters.set("numThread", args[7]);

//        parameters.set("threshold", "0.6");
//        parameters.set("numReducer", "1");
//        parameters.set("numThread", "4");

        parameters.set("partition_method", partitionMethod);
        if (partitionMethod.equals("prefix")) {
            useDeduplicateJob = true;
        }

        conf.set("dfs.replication", "1");
        System.out.println("treeType = " + treeType);
        System.out.println("rInputPath = " + rInputPath);
        System.out.println("sInputPath = " + sInputPath);
        System.out.println("outputPath = " + outputPath);
        System.out.println("partitionMethod = " + partitionMethod);
        System.out.println("threshold = " + threshold);
        System.out.println("numReducer = " + numReducer);
        System.out.println("numThread = " + numThread);
        if (treeType.equals("fpstree_split") || treeType.equals("telpstree_split")) {
            System.out.println("numSplitTree = " + numSplitTree);
        }
        Date startTime = new Date();
        double job1Time = generatePartitioner(conf, parameters);
        conf.set("fpsjoin.paramters", parameters.toString());
        FileOutputStream out = new FileOutputStream(logpath, true);
        PrintWriter writer = new PrintWriter(out);
        double job2Time = startMine(conf, parameters, writer);
        double job3Time = 0.0;
        Date endTime = new Date();
        System.out.println("Time is " + (endTime.getTime() - startTime.getTime())*1.0 / 1000L + " second");
        double jobTime = ((endTime.getTime() - startTime.getTime())*1.0 / 1000L);


        writer.write("treeType: " + treeType + "\n");
        writer.write("method: " + partitionMethod + "\n");
        writer.write("partitioner: " + parameters.get("parition") + "\n");
        writer.write("rInput: " + rInputPath + "\n");
        writer.write("sInput: " + sInputPath + "\n");
        writer.write("output: " + outputPath + "\n");
        writer.write("threshold: " + threshold + "\n");
        writer.write("numReducer: " + numReducer + "\n");
        writer.write("numThread: " + numThread + "\n");
        if (treeType.equals("fpstree_split") || treeType.equals("telpstree_split")) {
            writer.write("numSplitTree = " + numSplitTree + "\n");
        }
        writer.write("job1Time: " + job1Time + "\n");
        writer.write("job2Time: " + job2Time + "\n");
        if (useDeduplicateJob) {
            writer.write("job3Time: " + job3Time + "\n");
        }
        writer.write("jobTime: " + jobTime + "\n");
        writer.write("diskUsage: " + diskUsage/ 1048576.0 + " MB"+"\n");
        writer.write("allReadOps: " + allReadOps + "\n");
        writer.write("allWriteOps: " + allWriteOps + "\n");
        writer.close();
    }

    public static void waitJob(Job job) throws IOException, InterruptedException, ClassNotFoundException {
        boolean succeeded = job.waitForCompletion(true);
        if (!succeeded) {
            System.out.println("job = " + job);
            throw new IllegalStateException("Job failed!");
        } else {
            long writtenBytes = job.getCounters().findCounter("org.apache.hadoop.mapreduce.FileSystemCounter", "FILE_BYTES_WRITTEN").getValue();
            long readOps = job.getCounters().findCounter("org.apache.hadoop.mapreduce.FileSystemCounter", "HDFS_READ_OPS").getValue();
            long writeOps = job.getCounters().findCounter("org.apache.hadoop.mapreduce.FileSystemCounter", "HDFS_WRITE_OPS").getValue();
            diskUsage += writtenBytes;
            allReadOps += readOps;
            allWriteOps += writeOps;
        }
    }

    public static double generatePartitioner(Configuration conf, Parameters parameters) throws IOException {//在这里配置分发策略
        Date startTime = new Date();
        int numPartition = parameters.getInt("numReducer", 16);
        String method = parameters.get("partition_method", "random");
        System.out.println(method + ", " + numPartition);
        double threshold = parameters.getDouble("threshold", 1.0);
        PartitionerBase partitioner = PartitionerBase.getPartitioner(method, numPartition, threshold);
        FileSystem fs = FileSystem.get(conf);
        Path filePath = new Path(parameters.get("rInput"));
        FileStatus[] status = fs.listStatus(filePath);

        for(int i = 0; i < status.length; ++i) {
            Path inFile = new Path(status[i].getPath().toString());
            FSDataInputStream fin = fs.open(inFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

            String line;
            while((line = reader.readLine()) != null && partitioner.updateState(line)) {
            }
        }

        partitioner.initPartition();//初始化每个分区
        parameters.set("parition", partitioner.serialize());//将分区方法放入参数里，用于MR任务中的分区
        Date endTime = new Date();
        System.out.println("partitioner = " + partitioner);
        System.out.println("Partitioner: " + (endTime.getTime() - startTime.getTime())*1.0 / 1000L + " sec.");
        return ((endTime.getTime() - startTime.getTime())*1.0 / 1000L);
    }

    public static double startMine(Configuration conf, Parameters parameters, PrintWriter writer) throws IOException, InterruptedException, ClassNotFoundException {
        Date startTime = new Date();
        Job job = Job.getInstance(conf, "Mine");
        job.setJarByClass(RunMRv2.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(parameters.get("sInput")));
        FileInputFormat.addInputPath(job, new Path(parameters.get("rInput")));
        String subpath = "mine";
        if (useDeduplicateJob) {
            subpath = "deduplicate_input";
        }

        Path outputPath = new Path(parameters.get("output"), subpath);
        FileSystem fs = outputPath.getFileSystem(conf);
        if (fs.delete(outputPath, true)) {
            System.out.println("delete fail: " + outputPath);
        }

        FileOutputFormat.setOutputPath(job, outputPath);
        job.setMapperClass(NewFPSMineMapper.class);
        job.setReducerClass(NewFPSMineReducer.class);
        job.setNumReduceTasks(parameters.getInt("numReducer", 16));
        waitJob(job);
        Date endTime = new Date();

        writer.write("************FPSJoin************\n");
        FileSystem fs2 = FileSystem.get(job.getConfiguration());
        Path counterDIr = new Path(parameters.get("output") + "/" + "counter_output");
        RemoteIterator<LocatedFileStatus> files = fs2.listFiles(counterDIr,false);
        while(files.hasNext()){
            Path filePath = files.next().getPath();
            try(FSDataInputStream in = fs2.open(filePath)){
                String countStr = in.readUTF();
                writer.write(countStr);
            }
            fs2.delete(filePath,false);
        }

        return ((endTime.getTime() - startTime.getTime())*1.0 / 1000L);
    }
}

