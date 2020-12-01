package com.asn.structuredstreaming

import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * structured streaming是2.2以后出现的新的流处理api库（底层是dataset），用来替代spark streaming（底层是rdd）。
 */
object WordCount {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local[6]")
      .appName("structured-wordcount")
      .getOrCreate()

    spark.sparkContext.setLogLevel("error")

    import spark.implicits._

    //readStream创建一个source
    val df: DataFrame = spark.readStream
      .format("socket")
      .option("host", "192.168.18.101")
      .option("port", 9999)
      .load()

    //逻辑执行计划logicplan
    df.as[String].flatMap(_.split(" ")).createOrReplaceTempView("table")

    var wordcount = spark.sql(
        """
        |select *,count(*) count
        |from table
        |group by value
        """.stripMargin
    )

    //writeStream创建输出sink
    val result: StreamingQuery = wordcount.writeStream
      .format("console")
      .outputMode("update") //complete,append,update
      .start

    result.awaitTermination()
    spark.stop()

  }
}
