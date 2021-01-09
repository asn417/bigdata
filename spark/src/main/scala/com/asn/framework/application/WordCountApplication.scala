package com.asn.framework.application

import com.asn.framework.common.TApplication
import com.asn.framework.controller.WordCountController

object WordCountApplication extends App with TApplication{

  start(){
    val controller = new WordCountController
    controller.dispatch()
  }

}
