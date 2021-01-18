package com.asn.oop

import scala.util.Random

/**
 * @Author: wangsen
 * @Date: 2021/1/18 10:40
 * @Description: https://docs.scala-lang.org/zh-cn/tour/pattern-matching.html
 *              模式匹配是检查某个值（value）是否匹配某一个模式的机制，一个成功的匹配同时会将匹配值解构为其组成部分。
 *              它是Java中的switch语句的升级版，同样可以用于替代一系列的 if/else 语句。
 **/
object TestPatternMatch {
  def main(args: Array[String]): Unit = {
    val num: Int = Random.nextInt(10)
    println(matchNum(num))

    //样例类模式匹配
    def showNotification(notification: Notification): String = {
      notification match {
        case Email(sender, title, _) =>
          s"You got an email from $sender with title: $title"
        case SMS(number, message) =>
          s"You got an SMS from $number! Message: $message"
        case VoiceRecording(name, link) =>
          s"you received a Voice Recording from $name! Click the link to hear it: $link"
      }
    }
    val someSms = SMS("12345", "Are you there?")
    val someVoiceRecording = VoiceRecording("Tom", "voicerecording.org/id/123")
    println(showNotification(someSms))  // prints You got an SMS from 12345! Message: Are you there?
    println(showNotification(someVoiceRecording))  // you received a Voice Recording from Tom! Click the link to hear it: voicerecording.org/id/123

    //也可以仅匹配类型，当不同类型对象需要调用不同方法时，仅匹配类型的模式非常有用
    val email = Email("zhangsan","xx","nihao")
    def matchType(notification: Notification)=notification match {
      case e:Email => e.sendEmail(e.body)
      case s:SMS => s.sendSMS(s.message)
      case _ => println("no body")
    }
    matchType(email)



  }

  def matchNum(num:Int):String={
    num match {
      case 0 => "zero"
      case 1 => "one"
      case 2 => "two"
      case _ => "other"
    }
  }
}

abstract class Notification

case class Email(sender: String, title: String, body: String) extends Notification{
  def sendEmail(message:String)=println(message)
}

case class SMS(caller: String, message: String) extends Notification{
  def sendSMS(message:String)=println(message)
}

case class VoiceRecording(contactName: String, link: String) extends Notification
