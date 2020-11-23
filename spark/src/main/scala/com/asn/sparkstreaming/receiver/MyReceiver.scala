package com.asn.sparkstreaming.receiver

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

/**
 * @Author: wangsen
 * @Date: 2020/11/15 11:19
 * @Description: 自定义采集器
 **/
class MyReceiver(host:String,port:Int) extends Receiver[String](StorageLevel.MEMORY_ONLY){
  override def onStart(): Unit = {
    new Thread(() => {
      receive()
    }).start()
  }
  //socket对象没有继承Serializable，不可序列化，因此创建socket对象要在receive中写，也就相当于每个task都创建了一个socket
  private var socket:java.net.Socket =null
  def receive(): Unit ={
    socket =  new java.net.Socket(host, port)
    val reader = new BufferedReader(new InputStreamReader(socket.getInputStream,"UTF-8"))
    var line:String = null

    while ((line = reader.readLine()) != null){
      if ("END".equals(line)){
        return
      }else{
        //将采集的数据存储到采集器的内部进行封装转换
        this.store(line)
      }
    }

  }
  override def onStop(): Unit = {
    if (socket!= null){
      socket.close()
      socket=null
    }
  }
}
