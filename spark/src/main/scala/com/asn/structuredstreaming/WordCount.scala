package com.asn.structuredstreaming

import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, SparkSession}

object WordCount {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName("structured-wordcount")
      .getOrCreate()

    import spark.implicits._
    val df: DataFrame = spark.readStream
      .format("socket")
      .option("host", "flink1")
      .option("port", 9998)
      .load()

    df.as[String].flatMap(_.split(" ")).createOrReplaceTempView("table")


    var wordcount = spark.sql(
        """
        |select *,count(*) count
        |from table
        |group by value
        """.stripMargin
    )

    //输出
    val result: StreamingQuery = wordcount.writeStream
      .format("console")
      .outputMode("update") //complete,append,update
      .start

    result.awaitTermination()
    spark.stop()

  }
}
