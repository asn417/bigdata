package com.asn.sparkCore.rdd.action

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object SaveAction {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val context: SparkContext = new SparkContext(sparkConf)

    val value: RDD[(String,Int)] = context.makeRDD(List(("a",1),("a",2),("a",3),("a",4),("b",1),("b",2)), 2)
    value.cache()
    value.persist()
    value.persist(StorageLevel.MEMORY_ONLY)
    val value1: RDD[(String, Int)] = value.aggregateByKey(0)( //初始值
      (x, y) => math.max(x, y), //分区内逻辑
      (x, y) => x + y //分区间逻辑
    )

    value1.saveAsTextFile("")
    value1.saveAsObjectFile("")
    value1.saveAsSequenceFile("")
    value1.saveAsHadoopFile("")
    value1.saveAsHadoopDataset(null)

  }

}
