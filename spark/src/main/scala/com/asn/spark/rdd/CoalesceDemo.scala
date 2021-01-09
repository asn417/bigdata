package com.asn.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
/*
coalesce算子能缩减分区，但默认是不会进行shuffle的，也就是说缩减分区的方式是将若干个分区整体合并为一个分区，而不会将一个分区打散后放到多个分区。
 */
object CoalesceDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[Int] = context.makeRDD(List(1, 2, 3, 4, 5, 6), 3)
    //value.saveAsTextFile("spark\\src\\main\\resources\\data\\output")
    //val shuffleRDD: RDD[Int] = value.coalesce(2)
    val shuffleRDD: RDD[Int] = value.coalesce(2, true)

    shuffleRDD.saveAsTextFile("spark\\src\\main\\resources\\data\\output")

  }

}
