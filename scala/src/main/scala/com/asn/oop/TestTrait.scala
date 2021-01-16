package com.asn.oop

/**
 * @Author: wangsen
 * @Date: 2021/1/15 13:52
 * @Description: 特质
 *              Scala中没有接口的概念，特质类似于接口，但比接口强大。特质可以被继承extends，且可以继承多个with特质。
 *
 *              特质可以继承特质，特质之间的执行顺序比较复杂。
 **/
trait TestTrait {

  //可以有属性，且可以被子类修改
  var field:String
  //可以有方法

  //可以直接调用方法
}
