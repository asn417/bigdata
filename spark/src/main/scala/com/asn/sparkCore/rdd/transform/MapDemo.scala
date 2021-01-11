package com.asn.sparkCore.rdd.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author: wangsen
 * @Date: 2020/11/23 9:55
 * @Description:
 *              map:作用于分区内的每一条数据，处理一条返回一条，不会增加或减少分区的数据量
 *              mapPartitions:作用于整个分区，传入的是一个迭代器，迭代器负责处理分区中的每一条数据，返回的也是一个迭代器，因此对分区的数据可以做增删
 *              mapPartitionsWithIndex:可以通过分区号做更细粒度的操作
 **/
object MapDemo {
  def main(args: Array[String]): Unit = {
    //A master URL must be set in your configuration
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]")
    //An application name must be set in your configuration
    sparkConf.setAppName("map-demo")

    val context: SparkContext = new SparkContext(sparkConf)
    //textFile算子有一个最小分区数参数：minPartitions，如果没有指定，则根据math.min(defaultParallelism, 2)获取默认的分区数，其中defaultParallelism=scheduler.conf.getInt("spark.default.parallelism", totalCores)
    val value: RDD[String] = context.textFile("spark\\src\\main\\resources\\data\\word.txt")

    println(value.getNumPartitions)

    //flatmap通过split获取的就是打平的最后的值
    value.flatMap(x=>x.split(" ")).foreach(println)
    //map通过split之后获取的是数组对象，因此每个对象还需要遍历来获取最后的值
    value.map(x=>x.split(" ")).foreach(arr)
    //mapPartitions的作用范围是分区，而map是一个个元素，因此前者效率更高
    //但是由于会将整个分区的数据加载到 内存中，如果处理完的数据没有被释放，引用依然存在的话，就容易出现OOM。
    value.mapPartitions(
      iter=>{
        iter.map(x=>x.split(" "))
      }
    ).foreach(_.foreach(x=>println("mapPartitions==="+x)))

    //mapPartitionsWithIndex能够指定分区编号，来获取指定分区的数据
    value.mapPartitionsWithIndex(
      (index,iter)=>{
        if (index == 1){
          iter.map(x=>x.split(" ")).map((index,_))
        }else{
          Nil.iterator
        }
      }
    ).foreach(x=>{
      val index: Int = x._1
      x._2.foreach(x=>println("mapPartitionsWithIndex===index:"+index+",value:"+x))
    })


    context.stop()
  }
  def arr(array: Array[String]) = {
    array.foreach(x=>println("map==="+x))
  }
}
