package com.asn.dataframe

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
 * 与dataframe的read类似，dataframe也可以以多种格式写出，也可以写出到数据库
 */
object DataFrameWrite {
  def main(args: Array[String]): Unit = {
    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()
    //读取mysql表生成dataframe
    val dataFrame: DataFrame = sparkSession.read.format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/flink?serverTimezone=UTC")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "people")
      .option("user", "root")
      .option("password", "123456")
      .load()

    val result: Dataset[Row] = dataFrame.select("name", "age").where("age > 36")

    result.write.json("D:\\ideaProject\\bigdata\\spark\\src\\main\\resources\\data\\result.json")
    sparkSession.close()
  }
}
