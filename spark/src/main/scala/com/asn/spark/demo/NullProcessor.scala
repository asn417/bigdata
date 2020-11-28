package com.asn.spark.demo

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, StringType, StructField, StructType}
import org.junit.Test

/**
 * @Author: wangsen
 * @Date: 2020/11/28 17:33
 * @Description: 缺失值处理
 * 缺失值/空值的处理有两种，要么丢弃，要么用其他值填充。
 *
 **/
class NullProcessor {

  @Test
  def nullAndNaN():Unit={
    val spark: SparkSession = SparkSession.builder().master("local[*]").appName(this.getClass.getName).getOrCreate()

    //读取数据集
    //方式一、通过spark-csv自动推断数据类型，会将NaN推断为字符串
//    spark.read
//      .option("header",true)
//      .option("inferSchema",true)//自动推断
//      .csv("...")

    //方式二、直接读取字符串，在后续的操作中使用map算子转换类型
    //spark.read.csv().map(row=>row)

    //方式三、指定schema，不使用自动推断
    val schema = StructType(List(StructField("name",StringType),StructField("age",IntegerType),StructField("score",DoubleType)))

    val sourceDF: DataFrame = spark.read
      .option("header", true)
      .schema(schema)
      .csv("E:\\bigdata\\spark\\src\\main\\resources\\data\\table.csv")

    sourceDF.show()

    //当某行所有值都为null或NaN时，直接丢弃此行
    sourceDF.na.drop("all").show()

    //当某行指定列的值全为null或NaN时，直接丢弃此行
    sourceDF.na.drop("all",List("name","age")).show()
    sourceDF.na.drop("all",List("age","score")).show()
    //当某行数据任意一个字段为null或NaN时，直接丢弃此行
    sourceDF.na.drop("any").show()//drop的默认值就是any

    //当某行指定列中有值为null或NaN时，直接丢弃此行
    sourceDF.na.drop(List("name","age")).show()
    sourceDF.na.drop("any",List("name","age")).show()

    //针对所有列进行默认值填充
    sourceDF.na.fill(0).show()
    //针对指定列进行默认值填充
    sourceDF.na.fill(0,List("age","score")).show()


  }
}
