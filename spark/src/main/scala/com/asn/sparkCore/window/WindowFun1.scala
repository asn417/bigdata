package com.asn.sparkCore.window

import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @Author: wangsen
 * @Date: 2020/11/29 11:20
 * @Description: 利用窗口函数，获取每个类别收入最高的两个商品
 * rank、dense_rank和row_number三个排序函数的区别：
 *    1) rank会跳号，dense_rank会递增
 *    2) row_number不管排序的字段是否相同，一律递增排序，而rank和dense_rank对于排序字段相同的记录，会给与相同的序号
 **/
object WindowFun1 {
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

    //2.数据处理
    import org.apache.spark.sql.functions._
    data.select('product,'category,'revenue,dense_rank() over window as "rank")//dense_rank()是窗口函数，与SQL中的dense_rank函数功能相同
      .where('rank <= 2)
      .show()

    //也可以使用SQL的方式实现
    data.createOrReplaceTempView("productRevenue")
    spark.sql("select product,category,revenue from (select *,dense_rank() over (partition by category order by revenue desc) as rank from productRevenue) where rank <= 2")
      .show()

  }

}
