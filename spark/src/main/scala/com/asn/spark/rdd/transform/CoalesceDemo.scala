package com.asn.spark.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
coalesce算子能缩减分区，但默认是不会进行shuffle的，也就是说缩减分区的方式是将若干个分区整体合并为一个分区，而不会将一个分区打散后放到多个分区。
因此，如果使用coalesce算子进行扩大分区的话，必须指定shuffle。否则没有意义（不会扩大分区）。因此，spark又提供了一个repartition算子来实现
扩大分区，底层就是用的coalesce。
 */
object CoalesceDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[Int] = context.makeRDD(List(1, 2, 3, 4, 5, 6), 3)
    //value.saveAsTextFile("spark\\src\\main\\resources\\data\\output")
    //val shuffleRDD: RDD[Int] = value.coalesce(2)
    //val shuffleRDD: RDD[Int] = value.coalesce(2, true)
    //val shuffleRDD: RDD[Int] = value.coalesce(4, true)
    val shuffleRDD: RDD[Int] =  value.repartition(4);
    shuffleRDD.saveAsTextFile("spark\\src\\main\\resources\\data\\output")

  }

}
