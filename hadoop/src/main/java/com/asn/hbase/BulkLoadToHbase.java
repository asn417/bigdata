package com.asn.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * @Author: wangsen
 * @Date: 2021/1/5 10:31
 * @Description:
 **/
public class BulkLoadToHbase {
    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        Configuration conf= HBaseConfiguration.create();
        Connection conn= ConnectionFactory.createConnection(conf);
        Table table=conn.getTable(TableName.valueOf("BulkLoad"));
        Admin admin=conn.getAdmin();

        final String InputFile="hdfs://172.20.184.17:9000/HBaseTest/input";
        final String OutputFile="hdfs://172.20.184.17:9000/HBaseTest/output";
        final Path OutputPath=new Path(OutputFile);

        //设置相关类名
        Job job=Job.getInstance(conf,"BulkLoad");
        job.setJarByClass(BulkLoadToHbase.class);
        job.setMapperClass(GenerateHFileMapper.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Put.class);

        //设置文件的输入路径和输出路径
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(HFileOutputFormat2.class);
        FileInputFormat.setInputPaths(job, InputFile);
        FileOutputFormat.setOutputPath(job, OutputPath);

        //配置MapReduce作业，以执行增量加载到给定表中。
        HFileOutputFormat2.configureIncrementalLoad(job, table, conn.getRegionLocator(TableName.valueOf("BulkLoad")));

        //MapReduce作业完成，告知RegionServers在哪里找到这些文件,将文件加载到HBase中
        if(job.waitForCompletion(true)) {
            LoadIncrementalHFiles Loader=new LoadIncrementalHFiles(conf);
            Loader.doBulkLoad(OutputPath, admin, table, conn.getRegionLocator(TableName.valueOf("BulkLoad")));
        }

    }
}

class GenerateHFileMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put>{

    @Override
    protected void map(LongWritable Key, Text Value, Mapper<LongWritable, Text, ImmutableBytesWritable, Put>.Context context)
            throws IOException, InterruptedException {

        //切分导入的数据
        String Values=Value.toString();
        String[] Lines=Values.split("\t");
        String Rowkey=Lines[0];
        String ColumnFamily=Lines[1].split(":")[0];
        String Qualifier=Lines[1].split(":")[1];
        String ColValue=Lines[2];

        //拼装rowkey和put
        ImmutableBytesWritable PutRowkey=new ImmutableBytesWritable(Rowkey.getBytes());
        Put put=new Put(Rowkey.getBytes());
        put.addColumn(ColumnFamily.getBytes(), Qualifier.getBytes(), ColValue.getBytes());

        context.write(PutRowkey,put);
    }
}