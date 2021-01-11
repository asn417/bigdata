package com.asn.sparkCore.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
 * @Author: wangsen
 * @Date: 2020/11/17 10:00
 * @Description: join会将两个rdd中具有相同键的元素取出，然后这些元素根据键进行两两配对。join可以用cogroup结合flatmap实现
 **/
object JoinDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val sparkContext = new SparkContext(sparkConf)

    val rdd1: RDD[(Int, Int)] = sparkContext.makeRDD(List((1,2),(1,2),(1,3),(2,2)))
    val rdd2: RDD[(Int, Int)] = sparkContext.makeRDD(List((1,1),(1,2),(1,5),(2,1),(3,2)))

    //join
    val joinRDD: RDD[(Int, (Int, Int))] = rdd1.join(rdd2)
    joinRDD.foreach(println)

    println("=======================")

    //用cogroup+flatmap实现join功能
    val cogroupRDD: RDD[(Int, (Iterable[Int], Iterable[Int]))] = rdd1.cogroup(rdd2)//[()]
    val value: RDD[(Int, (Int, Int))] = cogroupRDD.flatMap(x => {
      val arr = new ArrayBuffer[(Int, (Int, Int))]()//可变数组
      val key: Int = x._1
      val value1: Iterable[Int] = x._2._1
      val value2: Iterable[Int] = x._2._2
      for (v1 <- value1) {
        for (v2 <- value2) {
          arr += ((key, (v1, v2)))//向可变数组中放入值
        }
      }
      arr
    })
    value

    value.foreach(println)

    sparkContext.stop()
  }
}
