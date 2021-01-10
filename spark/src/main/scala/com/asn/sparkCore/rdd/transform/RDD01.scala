package com.asn.sparkCore.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author: wangsen
 * @Date: 2020/11/12 22:46
 * @Description:
 **/
object RDD01 {
  def main(args: Array[String]): Unit = {
    val wc: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wc")

    val context: SparkContext = new SparkContext(wc)

    //1.makeRDD
    val listRDD: RDD[Int] = context.makeRDD(List(1,2,3,4))
    listRDD.collect().foreach(println)

    listRDD.saveAsTextFile("output")
    //2.parallelize
    val arrayRDD: RDD[Int] = context.parallelize(Array(1,2,3,4))

    //3.textFile
    val textFileRDD: RDD[String] = context.textFile("")
  }

}
