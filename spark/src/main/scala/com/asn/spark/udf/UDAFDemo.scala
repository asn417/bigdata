package com.asn.spark.udf

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction, UserDefinedFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}

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
//声明用户自定义的聚合函数
class MyAgeAvgFunction extends UserDefinedAggregateFunction{
  //数据源的结构
  override def inputSchema: StructType = {
    new StructType().add("age",LongType)
  }

  //计算时的数据结构
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
