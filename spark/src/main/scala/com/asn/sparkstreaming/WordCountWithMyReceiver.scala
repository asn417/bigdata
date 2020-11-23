package com.asn.sparkstreaming

import com.asn.sparkstreaming.receiver.MyReceiver
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.ReceiverInputDStream

/**
 * @Author: wangsen
 * @Date: 2020/11/15 11:38
 * @Description:
 **/
object WordCountWithMyReceiver {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming-wordcount-withMyReceiver")

    //每三秒为一批数据，因此每次统计的都是这5秒内的数据
    val streamingContext: StreamingContext = new StreamingContext(sparkConf,Seconds(3))

    val socketStreaming: ReceiverInputDStream[String] = streamingContext.receiverStream(new MyReceiver("flink1",9999))

    socketStreaming.flatMap((line: String) =>line.split(" "))
      .map(word=>(word,1))
      .reduceByKey(_+_)
      .print()

    streamingContext.start()

    streamingContext.awaitTermination()
  }
}
