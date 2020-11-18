package com.asn.spark.kafka


import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils, OffsetRange}
/**
 * @Author: wangsen
 * @Date: 2020/11/15 13:27
 * @Description: sparkstreaming 消费kafka，在kafka0.10+版本中，已经不支持receiver的消费方式，只支持direct方式
 **/
object KafaStreamingDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming-wordcount")

    //每三秒为一批数据，因此每次统计的都是这3秒内的数据
    val streamingContext: StreamingContext = new StreamingContext(sparkConf,Seconds(3))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "flink1:9092,flink2:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "spark_group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)//关闭自动提交offset
    )

    val topics = Array("topicA", "topicB")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD(eachRdd =>{
      if(!eachRdd.isEmpty()){
        eachRdd.foreachPartition(eachPartition =>{
          eachPartition.foreach(record =>{
            println((record.key(),record.value()))
          })
        })
        //准备更新offset的值  获取到了offset的值，更新到hbase里面去即可
        val offsetRanges: Array[OffsetRange] = eachRdd.asInstanceOf[HasOffsetRanges].offsetRanges
        stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }
    })

    streamingContext.start()

    streamingContext.awaitTermination()
  }
}
