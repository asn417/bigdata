package com.asn.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
//使用开发工具完成spark wordcount的开发
object WordCount {
  def main(args: Array[String]): Unit = {
    //创建spark程序的运行环境（部署环境）
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordcount")
    val sc = new SparkContext(sparkConf)
    //print(sc)
    //本地读取方式：file://
    //hdfs读取方式：hdfs://
    val lines: RDD[String] = sc.textFile("file:///opt/bigdata/spark-3.0.0-bin-hadoop3.2/data/test/words.txt")

    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String,Int)] = words.map((_,1))
    
    val wordToSum: RDD[(String,Int)] = wordToOne.reduceByKey(_+_)

    val result: Array[(String, Int)] = wordToSum.collect()

    result.foreach(println)
  }
}
