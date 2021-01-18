package com.asn.sparkCore.rdd.transform

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @Author: wangsen
 * @Date: 2021/1/18 11:47
 * @Description: 将同一个分区的数据直接转换为相同类型的内存数组进行处理，分区不变
 **/
object GlomDemo {
  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[Int] = context.makeRDD(List(1, 2, 3, 4, 5, 6), 3)

    //glom将分区内的数据转换成数组
    val array1: Array[Array[Int]] = value.glom().collect()

    /**
     * 下面的data就是分区数组
     * 1,2
     * 3,4
     * 5,6
     **/
    array1.foreach(data=>println(data.mkString(",")))
    //用glom求各个分区的最大值之和
    val d: Double = value.glom().map(arr => {
      arr.max
    }).sum()

    println(d)

    context.stop()
  }

}
