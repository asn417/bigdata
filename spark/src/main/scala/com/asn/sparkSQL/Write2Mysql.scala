package com.asn.sparkSQL

import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.types.{FloatType, StringType, StructField, StructType}

/**
 * @Author: wangsen
 * @Date: 2020/11/27 11:58
 * @Description: sparkSQL读写mysql
 * mysql有两种访问方式：在本地访问，和在集群访问
 *
 * 这里以本地方式写入数据，以集群方式读取数据
 **/
object Write2Mysql {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("mysql write")
      .getOrCreate()
    spark

    val structType = StructType(List(StructField("name",StringType),StructField("grade",FloatType),StructField("subject",StringType),StructField("year",StringType)))

    val df: DataFrame = spark.read
      .schema(structType)
      .json("E:\\bigdata\\spark\\src\\main\\resources\\data\\grade.json")
    df

    df.show()

    val result: Dataset[Row] = df.where("grade > 90")

    result.write
      .format("jdbc")
      .option("url","jdbc:mysql://localhost/big_data?serverTimezone=Asia/Shanghai")
      .option("driver","com.mysql.cj.jdbc.Driver")
      .option("dbtable","grade")
      .option("user","wangsen")
      .option("password","root")
      .mode(SaveMode.Overwrite)
      .save()
  }
}
