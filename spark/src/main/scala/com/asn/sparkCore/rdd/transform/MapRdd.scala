package com.asn.sparkCore.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object MapRdd {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setAppName("my-map-rdd").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)

    val source: RDD[String] = sc.textFile("spark\\src\\main\\resources\\data\\word.txt")

    val wc: RDD[(String, Int)] = source
      .flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    wc.foreach(println)
  }

  def myFlatMap(line:String):String={
    "xxx"
  }
  def myMap(word:String):(String,Int)={
    (word,1)
  }
}
