package com.asn.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    //使用开发工具完成spark wordcount的开发

    //创建spark程序的运行环境（部署环境）
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordcount")
    val sc = new SparkContext(sparkConf)
    //print(sc)
    val lines: RDD[String] = sc.textFile("D:\\ideaProject\\hadoop\\spark\\src\\main\\resources\\word.txt")

    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne: RDD[(String,Int)] = words.map((_,1))
    
    val wordToSum: RDD[(String,Int)] = wordToOne.reduceByKey(_+_)

    val result: Array[(String, Int)] = wordToSum.collect()

    result.foreach(println)
  }
}
