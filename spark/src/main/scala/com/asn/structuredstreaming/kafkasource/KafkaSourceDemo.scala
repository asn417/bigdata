package com.asn.structuredstreaming.kafkasource

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.StreamingQuery

/**
 * http://spark.apache.org/docs/3.0.0/structured-streaming-kafka-integration.html
 */
object KafkaSourceDemo {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName("structured-kafkasource")
      .getOrCreate()
    import spark.implicits._
    // Subscribe to 1 topic
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribe", "topicA")
      .load()
    val result: StreamingQuery = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
      .writeStream
      .format("console")
      .outputMode("append")
      .start

    result.awaitTermination()
    spark.stop()
    /*// Subscribe to 1 topic, with headers
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribe", "topic1")
      .option("includeHeaders", "true")
      .load()
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)", "headers")
      .as[(String, String, Map)]

    // Subscribe to multiple topics
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribe", "topic1,topic2")
      .load()
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]

    // Subscribe to a pattern
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "flink1:9092,flink2:9092")
      .option("subscribePattern", "topic.*")
      .load()
    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]*/
  }

}
