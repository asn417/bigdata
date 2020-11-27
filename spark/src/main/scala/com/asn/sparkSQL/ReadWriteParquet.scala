package com.asn.sparkSQL

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/25 17:51
 * @Description: 读写parquet文件
 **/
object ReadWriteParquet {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .getOrCreate()

    //读取CSV格式文件
    val df: DataFrame = spark.read
      .option("header", false)
      .csv("E:\\bigdata\\spark\\src\\main\\resources\\data\\bank-additional-full.csv")
    df

    //写出到parquet格式文件
    df.write
      .format("parquet")//默认就是parquet
      .mode(SaveMode.Append)//追加模式
      .save("output/parquet")

    //读取parquet格式文件(默认读取的格式也是parquet)
    spark.read.load("output/parquet").show()

  }

}
