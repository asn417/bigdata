package com.asn.spark.demo

import org.apache.spark.sql.SparkSession
import org.junit.Test

class AggProcessor {

  private val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName(this.getClass.getName)
    .getOrCreate()
}
