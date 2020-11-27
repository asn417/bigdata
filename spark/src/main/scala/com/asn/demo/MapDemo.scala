package com.asn.demo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @Author: wangsen
 * @Date: 2020/11/23 9:55
 * @Description:
 **/
object MapDemo {
  def main(args: Array[String]): Unit = {
    //A master URL must be set in your configuration
    val sparkConf: SparkConf = new SparkConf().setMaster("local")
    //An application name must be set in your configuration
    sparkConf.setAppName("map-demo")

    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[String] = context.textFile("E:\\bigdata\\spark\\src\\main\\resources\\data\\word.txt")

    println(value.getNumPartitions)

    value.flatMap(x=>x.split(" ")).foreach(println)

    value.map(x=>x.split(" ")).foreach(println)


    context.stop()
  }
}
