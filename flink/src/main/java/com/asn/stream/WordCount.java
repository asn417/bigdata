package com.asn.stream;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 流的方式处理批数据
 */
public class WordCount {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> source = env.readTextFile("D:\\ideaProject\\bigdata\\flink\\src\\main\\resources\\data\\word.txt");

        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = source.flatMap(new com.asn.batch.WordCount.MyFlatMapper()).keyBy(0).sum(1);

        sum.print();

        env.execute();
    }
}
