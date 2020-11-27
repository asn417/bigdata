package com.asn.rdd

import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/27 17:16
 * @Description: 增加列
 **/
object WithColumnDemo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[3]").appName(this.getClass.getName).getOrCreate()

    val seq: Seq[StudentVo] = Seq(StudentVo("a",10),StudentVo("b",15),StudentVo("c",8),StudentVo("a",8),StudentVo("d",0))
    import spark.implicits._
    val ds: Dataset[StudentVo] = seq.toDS()

    ds.show()

    import org.apache.spark.sql.functions._
    //有两种方式来使用函数功能
    //1. 使用function.xx
    //2. 使用expr表达式
    ds.withColumn("newCol",expr("rand()")).show()//新加一列名为newCol，值为随机数

    ds.withColumn("newCol1",'name).show()//增加一列名为newCol1，并用name列的值填充

    ds.withColumn("newCol2",'name==="").show()//增加一列名为newCol2，并用name的值是否为""填充，true或false

    ds.withColumnRenamed("name","newName").show()//修改列名称

  }
}