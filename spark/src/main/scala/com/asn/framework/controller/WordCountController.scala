package com.asn.framework.controller

import com.asn.framework.common.TController
import com.asn.framework.service.WordCountService

class WordCountController extends TController{
  private val wordCountService = new WordCountService

  def dispatch()={
    var array = wordCountService.dataAnalysis()
    array.foreach(println)
  }
}
