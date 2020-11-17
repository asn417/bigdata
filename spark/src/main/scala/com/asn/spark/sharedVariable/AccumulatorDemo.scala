package com.asn.spark.sharedVariable

import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author: wangsen
 * @Date: 2020/11/17 15:39
 * @Description: 累加器使用
 * 1、累加器只能进行增加操作
 * 2、spark提供了三种内置累加器：LongAccumulator、DoubleAccumulator、CollectionAccumulator[T].
 * 3、处理内置的三种累加器，还可以通过继承AccumulatorV2自定义累加器
 **/
object AccumulatorDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("accumulator-demo")
    val sparkContext = new SparkContext(sparkConf)

    val acc1: LongAccumulator = sparkContext.longAccumulator("acc1")
    val rdd: RDD[Int] = sparkContext.makeRDD(List(1,2,3,4,5,6))

    //累加器只有在执行count算子的时候才会进行计算
    val count: Long = rdd.map(x=>{acc1.add(1);x}).count()

    println(count)
    sparkContext.stop()
  }
}
