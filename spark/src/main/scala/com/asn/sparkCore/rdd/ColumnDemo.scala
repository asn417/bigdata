package com.asn.sparkCore.rdd

import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/27 17:44
 * @Description:
 **/
object ColumnDemo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[3]").appName(this.getClass.getName).getOrCreate()

    val seq: Seq[StudentVo] = Seq(StudentVo("a",10),StudentVo("b",15),StudentVo("c",8),StudentVo("a",8),StudentVo("d",0))
    import spark.implicits._
    val ds: Dataset[StudentVo] = seq.toDS()

    //创建Column对象


    //Column相关API
  }
}
