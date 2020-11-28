package com.asn.spark.demo

import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/27 16:23
 * @Description:
 **/
object OrderByDemo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[*]").appName("orderByDemo").getOrCreate()

    import spark.implicits._
    val ds: Dataset[StudentVo] = spark.createDataset(Seq(StudentVo("a",10),StudentVo("b",15),StudentVo("c",8),StudentVo("a",8),StudentVo("d",0)))

    ds.orderBy("age","name").show()//先age升序，再name升序
    ds.orderBy('age,'name).show()//同上
    ds.orderBy('age.asc,'name.desc).show()//先age升序，再name降序
    ds.orderBy('age.asc_nulls_first).show()//age升序，null值放在最前面
    ds.orderBy('age.asc_nulls_last).show()

    ds.sort('age).show()//同orderBy,orderBy底层也是调用的sort
  }
}
case class StudentVo(name:String,age:Int)