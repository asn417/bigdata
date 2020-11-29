package com.asn.spark.window

import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/29 11:20
 * @Description: 利用窗口函数，获取每个商品与对应类别最大价格的价差
 **/
object WindowFun2 {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName(this.getClass.getName)
      .getOrCreate()
    spark

    import spark.implicits._

    val data: DataFrame = Seq(
      ("Thin", "Cell phone", 6000),
      ("Normal", "Tablet", 1500),
      ("Mini", "Tablet", 5500),
      ("Ultra thin", "Cell phone", 5000),
      ("Very thin", "Cell phone", 6000),
      ("Big", "Tablet", 2500),
      ("Bendable", "Cell phone", 3000),
      ("Foldable", "Cell phone", 3000),
      ("Pro", "Tablet", 4500),
      ("Pro2", "Tablet", 6500)
    ).toDF("product", "category", "revenue")
    data

    //1.定义窗口
    val window: WindowSpec = Window.partitionBy('category)
      .orderBy('revenue.desc)
    window

    //2.找到最贵的商品价格
    import org.apache.spark.sql.functions._
    val maxPrice: Column = max('revenue) over window

    //3.得到结果
    data.select('product,'category,'revenue,(maxPrice - 'revenue) as "revenue_difference").show()

  }

}
