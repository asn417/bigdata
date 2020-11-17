package com.asn.dataframe

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

//DataFrame支持多种数据源和文件格式，包括json、CSV、parquet、orc、text、jdbc等
//还可以自定义schema，将RDD反射成有结构的DataFrame
//json、parquet、orc、jdbc这些数据源是自带schema的，其他没有schema的数据源，dataframe会自己生成
object DataFrameRead {
  def main(args: Array[String]): Unit = {

    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    val frame: DataFrame = sparkSession.read.json("D:\\ideaProject\\bigdata\\spark\\src\\main\\resources\\data\\person.json")
    /*sparkSession.read.parquet()
    sparkSession.read.orc()
    sparkSession.read.text()
    sparkSession.read.csv()*/
    frame.show()

    val value: Dataset[Row] = frame.select("name").where("age > 36")
    value.show()
    //读取mysql表生成dataframe
    /*val dataFrame: DataFrame = sparkSession.read.format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/flink?serverTimezone=UTC")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", "people")
      .option("user", "root")
      .option("password", "123456")
      .load()
    dataFrame.show()*/

    sparkSession.close()
  }
}
