/**
 * @Author: wangsen
 * @Date: 2020/11/13 14:10
 * @Description:
 **/
//object 声明的对象为伴生对象(编译后会而外产生一个后缀为$的class文件，即为伴生对象)，模拟java中的静态对象，可以直接通过伴生对象的名字调用其内部的方法和属性
//主方法必须定义在伴生对象中才能运行
object HelloWorld {
  //scala中定义变量必须显式初始化值，_代表对应类型的默认值
  //一个语句结尾可以不加;，但如果多条语句写在一行的时候，中间必须用;隔开
  var name:String=_
  var age: Int = _
  //main方法编译后会自动编译成public static void main
  def main(args: Array[String]): Unit = {
    //变量
    var myVar:String ="asn"
    //常量
    val myVal:String ="aaa"
    //插值字符串
    var s=s"${myVar}---${myVal}"
    println(s)
    println(s"myVar=${myVar},myVal=${myVal}")
    printf("myVar=%s,myVal=%s",myVar,myVal)

    //使用object声明的伴生对象类似于java中的静态对象，可以直接调用其内部的方法
    HelloWorld.setName("hello")
    println(HelloWorld.getName())
    println(HelloWorld.getName1())
    HelloWorld.getName2
    HelloWorld.setName1("world")
    HelloWorld.getName2

    HelloWorld.setName3()
    HelloWorld.getName2
    HelloWorld.setName3("dwade")
    HelloWorld.getName2

    HelloWorld.setName4(age=10)
    HelloWorld.show
  }
  //def用来定义方法，方法形参的格式和java的不同，要先写参数名，然后写参数类型，方法的返回值类型也是在方法名后面指定，然后在大括号内定义方法体，并用等号赋给方法名
  def getName():String={
    this.name
  }

  //没有返回值的方法，用Unit表示，相当于java中的void关键字
  def setName(name:String):Unit={
    this.name=name
  }

  //如果方法体中只有一条语句，则可以省略掉大括号
  def getName1()=this.name
  //如果方法没有参数列表，则可以省略参数小括号，但调用此方法时方法后也不能有小括号
  def getName2=println(this.name)
  //如果方法没有返回值，可以将=号省略
  def setName1(name:String){this.name=name}

  //可变参数
  def setName2(name:String*): Unit ={
    println(name)
  }

  //带默认值的参数，调用的时候可以不传参数
  def setName3(name:String="default name"): Unit ={
    this.name=name
  }

  def show{println(this.name,this.age)}

  //带名参数，在调用方法的时候可以指定将参数传给哪个参数，解决形参顺序和实参顺序不一致的问题
  def setName4(name:String="default name1",age:Int): Unit ={
    this.age=age
    this.name=name
  }

}

