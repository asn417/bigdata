package com.asn.spark.sharedVariable

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
 * @Author: wangsen
 * @Date: 2020/11/17 14:35
 * @Description: 广播变量的使用
 *              广播变量会占用内存，可以使用unpersist将变量移除，再次使用的时候会重新拉取。
 *              也可以使用destory彻底消化广播变量，销毁后如果有任务再次使用这个变量，则会抛出异常。
 **/
object BroadcastDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("broadcast-demo")
    val sparkContext = new SparkContext(sparkConf)

    //要共享的变量
    val sharedVariables: mutable.HashMap[Int, Int] = scala.collection.mutable.HashMap(1->2,2->3,3->4)
    //将共享变量广播出去
    val value: Broadcast[mutable.HashMap[Int, Int]] = sparkContext.broadcast(sharedVariables)

    //value.unpersist()
    //value.destroy()

    //使用广播变量
    val rdd: RDD[Int] = sparkContext.makeRDD(List(1,2,3)).map(num=>num*value.value(num))

    rdd.foreach(println)

    sparkContext.stop()
  }
}
