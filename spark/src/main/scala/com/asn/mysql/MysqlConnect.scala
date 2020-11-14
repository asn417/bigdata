package com.asn.mysql

import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author: wangsen
 * @Date: 2020/11/14 14:40
 * @Description:
 **/
object MysqlConnect {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wordcount")
    val sc = new SparkContext(sparkConf)

    val driver="com.mysql.jdbc.Driver"
    val url="jdbc:mysql://localhost/big_data"
    val username=""
    val password=""

    val sql=""
    val value = new JdbcRDD[Array[Object]](sc, () => {
      Class.forName(driver)
      java.sql.DriverManager.getConnection(url, username, password)
    }, sql, 1, 3, 2)
    value

    value.collect()
    sc.stop()
  }
}
