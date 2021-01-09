package com.asn.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/*
partitionBy算子能够指定分区器对数据进行重分区。默认的分区器是HashPartitioner。
 */
object PartitionByDemo {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[Int] = context.makeRDD(List(1, 2, 3, 4, 5, 6), 3)

    val kv: RDD[(Int, Int)] = value.map((_, 1))

    val shuffleRDD: RDD[(Int, Int)] = kv.partitionBy(new HashPartitioner(2));
    shuffleRDD.saveAsTextFile("spark\\src\\main\\resources\\data\\output")

  }

}
