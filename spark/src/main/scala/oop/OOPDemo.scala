package oop

object OOPDemo {
  def main(args: Array[String]): Unit = {
    val user: User = new User()
    user.userName="zhangsan"
    println(user.userName)

  }
}

class User{
  var userName:String=_
  var age:Int=_
  def login():Boolean={
    true
  }
}


