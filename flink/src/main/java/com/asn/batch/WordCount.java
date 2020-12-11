package com.asn.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * 批的方式处理批数据
 */
public class WordCount {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<String> source = env.readTextFile("D:\\ideaProject\\bigdata\\flink\\src\\main\\resources\\data\\word.txt");
        //DataSet<String> source = env.readTextFile("D:\\ideaProject\\bigdata\\flink\\src\\main\\resources\\data\\word.txt");

        AggregateOperator<Tuple2<String, Integer>> sum = source.flatMap(new MyFlatMapper()).groupBy(0).sum(1);
        //DataSet<Tuple2<String, Integer>> sum = source.flatMap(new MyFlatMapper()).groupBy(0).sum(1);

        sum.print();
    }

    public static class MyFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>>{

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            String[] words = value.split(" ");
            for (String word :
                    words) {
                Tuple2<String, Integer> t = new Tuple2<>();
                t.f0 = word;
                t.f1 = 1;
                out.collect(t);
            }
        }
    }
}
