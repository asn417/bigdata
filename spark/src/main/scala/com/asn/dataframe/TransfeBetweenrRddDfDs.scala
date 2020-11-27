package com.asn.dataframe

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/14 17:01
 * @Description:
 **/
object TransferRddAndDF {

  def main(args: Array[String]): Unit = {

    val sparkSession: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    //创建rdd
    val rdd: RDD[(Int, String, Int)] = sparkSession
      .sparkContext.makeRDD(List((1,"zhangsan",20),(2,"lisi",30),(3,"wangwu",20)))

    //rdd转DF
    //转换之前，需要引入隐式转换规则
    import sparkSession.implicits._

    val df: DataFrame = rdd.toDF("id","name","age")

    //DF转DS
    val ds: Dataset[User] = df.as[User]

    //DS转DF
    val df1: DataFrame = ds.toDF()

    //DF转RDD（转换回RDD时，数据类型变为Row）
    val rdd1: RDD[Row] = df1.rdd

    //RDD直接转DS
    val userRDD:RDD[User] = rdd.map{
      case (id,name,age)=>{
        User(id,name,age)
      }
    }
    val DS1: Dataset[User] = userRDD.toDS()


    rdd1.foreach(row=>{
      //获取数据时可以通过索引指定获取那一列数据
      println(row.getString(1))
    })

    sparkSession.stop()
  }
}
case class User(id:Int,name:String,age:Int)
