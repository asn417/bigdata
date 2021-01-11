package com.asn.sparkSQL.udaf

import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, Row, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/14 17:23
 * @Description: 自定义聚合函数
 **/
object UDAFDemo {
  def main(args: Array[String]): Unit = {

    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    //创建聚合函数对象
    val myAgeAvgFunction = new MyAgeAvgFunction

    //注册聚合函数
    sparkSession.udf.register("avgAge",myAgeAvgFunction)
    //使用聚合函数
    val frame: DataFrame = sparkSession.read.json("E:\\bigdata\\spark\\src\\main\\resources\\person.json")
    frame.createOrReplaceTempView("user")

    sparkSession.sql("select avgAge(age) from user").show()

    sparkSession.stop()

  }
}
case class User(name:String,age:Long)
case class Avg(var sum:Long,var count:Long)
//强类型
class MyAvgDs extends Aggregator[User,Avg,Double]{
  override def zero: Avg = {
    Avg(0,0)
  }
  //聚合操作
  override def reduce(b: Avg, a: User): Avg ={
    b.sum = a.age + b.sum
    b.count = b.count + 1
    b
  }
  //合并缓存
  override def merge(b1: Avg, b2: Avg): Avg = {
    b1.sum = b1.sum + b2.sum
    b1.count = b1.count + b2.count
    b1
  }
  //返回值
  override def finish(reduction: Avg): Double = {
    reduction.sum.toDouble/reduction.count
  }
  //
  override def bufferEncoder: Encoder[Avg] = Encoders.product

  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}
//声明用户自定义的聚合函数,弱类型（依赖字段顺序做计算）
class MyAgeAvgFunction extends UserDefinedAggregateFunction{
  //数据源的数据结构
  override def inputSchema: StructType = {
    new StructType().add("age",LongType)
  }

  //计算时缓冲区中的数据结构
  override def bufferSchema: StructType = {
    new StructType().add("sum",LongType).add("count",LongType)
  }

  //函数返回的数据类型
  override def dataType: DataType = DoubleType

  //函数是否稳定
  override def deterministic: Boolean = true

  //计算之前的缓冲区如何初始化
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0L
  }

  //根据查询结果更新缓冲区数据
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getLong(0) + input.getLong(0)
    buffer(1) = buffer.getLong(1) + 1
  }

  //将多个节点的缓冲区合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    //sum
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    //count
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  //最终计算
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0).toDouble / buffer.getLong(1)
  }
}
