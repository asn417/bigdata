package com.asn.spark.sharedVariable

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.util.AccumulatorV2

/**
 * @Author: wangsen
 * @Date: 2020/11/17 15:56
 * @Description: 自定义累加器
 **/
object CustomerAccumulatorDemo  {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("CustomerAccumulatorDemo")
    val sc = new SparkContext(sparkConf)

    val fieldAcc = new FieldAccumulator
    sc.register(fieldAcc,"fieldAcc")
    val tableRDD: RDD[String] = sc.textFile("E:\\bigdata\\spark\\src\\main\\resources\\table.csv").filter(_.split(",")(0) != "A")

    val count: Long = tableRDD.map(x => {
      val fields: Array[String] = x.split(",")
      val a: Int = fields(0).toInt
      val b: Int = fields(1).toInt
      fieldAcc.add(SumAandB(a, b))
      x
    }).count()
    count

    println(count)

    sc.stop()
  }
}

case class SumAandB(A: Long, B: Long)
class FieldAccumulator extends AccumulatorV2[SumAandB,SumAandB] {
  private var A:Long = 0L
  private var B:Long = 0L
  // 如果A和B同时为0，则累加器值为0
  override def isZero: Boolean = A == 0 && B == 0L
  // 复制一个累加器
  override def copy(): FieldAccumulator = {
    val newAcc = new FieldAccumulator
    newAcc.A = this.A
    newAcc.B = this.B
    newAcc
  }
  // 重置累加器为0
  override def reset(): Unit = { A = 0 ; B = 0L }
  // 用累加器记录汇总结果
  override def add(v: SumAandB): Unit = {
    A += v.A
    B += v.B
  }
  // 合并两个累加器
  override def merge(other: AccumulatorV2[SumAandB, SumAandB]): Unit = {
    other match {
      case o: FieldAccumulator => {
        A += o.A
        B += o.B}
      case _ =>
    }
  }
  // 当Spark调用时返回结果
  override def value: SumAandB = SumAandB(A,B)
}