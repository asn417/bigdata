package com.asn.sparkSQL.udf

import org.apache.spark.sql.{DataFrame, SparkSession, functions}

/**
 * @Author: wangsen
 * @Date: 2021/2/24 16:47
 * @Description: 两种方式：1、register 2、functions
 **/
object UDFDemo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName("SparkUdfStudy").getOrCreate()
    import spark.implicits._
    val df: DataFrame = Seq((1, "foo"), (2, "bar")).toDF("id", "text")
    // register方式
    spark.udf.register("to_uppercase", (s: String) => s.toUpperCase())
    df.createOrReplaceTempView("t_foo")
    spark.sql("select id, to_uppercase(text) text from t_foo").show()

    //functions方式
    val toUpperCase = functions.udf((s: String) => s.toUpperCase)
    df.withColumn("text", toUpperCase('text)).show()
  }

}
