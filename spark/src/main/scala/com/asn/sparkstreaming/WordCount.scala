package com.asn.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext, dstream}

/**
 * @Author: wangsen
 * @Date: 2020/11/14 18:16
 * @Description: 使用netcat产生数据，sparkstreaming实时读取进行单词统计
 **/
object WordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming-wordcount")

    //每三秒为一批数据，因此每次统计的都是这3秒内的数据
    val streamingContext: StreamingContext = new StreamingContext(sparkConf,Seconds(3))

    val socketStreaming: ReceiverInputDStream[String] = streamingContext.socketTextStream("flink1",9999)



    val mapValue: DStream[(String, Int)] = socketStreaming.flatMap(line=>line.split(" ")).map(word=>(word,1))

    //加上状态更新操作(checkpoint)，以便实现对连续不断的微批数据的统计，而不是仅统计当前批次
    streamingContext.sparkContext.setCheckpointDir("checkpoint")//设置检查点报错路径
    val stateValue: DStream[(String, Int)] = mapValue.updateStateByKey {
      case (seq, buffer) => {
        val sum: Int = buffer.getOrElse(0) + seq.sum
        Option(sum)
      }
    }

    //stateValue.print()
    stateValue.reduceByKey(_+_).print()

    streamingContext.start()

    streamingContext.awaitTermination()

  }
}
