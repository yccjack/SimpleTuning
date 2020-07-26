# SimpleTuning
Simple JVM  Tuning simulation

# 运行
> * 设置参数 有一点需要注意的是，如果-Xms和-Xmx没有被设定成相同的值，而且-Xmn被使用了，当调整Java堆的大小的时候，
 >*  vm_1 : 默认：-Xms:默认为物理内存的1/64 -Xmx:默认为物理内存的1/4或者1G,
 因为存在堆空间扩容，第一次运行的时候会执行多次FULL GC,通过关闭自适应调整策略(-XX:-UseAdaptiveSizePolicy)，
 JVM已经事先被禁止动态调整内存池的大小。
> * -XX:+PrintGCDetails
 > * -XX:+UseG1GC -XX:+UseConcMarkSweepGC -XX:+UseParallelGC -XX:+UseSerialGC  除了使用G1算法外，其他的算法实际返回用户可视化的可用空间都将少一个Survivor区的大小的空间
 >  -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime 获取完整的安全点日志
     -Djava.util.concurrent.ForkJoinPool.common.parallelism=核数*2    IO操作时会有很多CPU处在闲置，使用默认线程池个数(机器核数)这样可能会丢失7%的性能，此参数修改ForkJoin的线程池个数，
    
>*  vm_1 : 默认：-Xms:默认为物理内存的1/64 -Xmx:默认为物理内存的1/4或者1G
 >*  vm_2 : -Xms750m -Xms2048m -Xmx2048m
 >*  vm_3 : -Xms1024m -Xms2048m -Xmx2048m
 >*  vm_4 : -Xms1024m -Xms3096m -Xmx3096m
 >*  vm_5 : -Xms250m -Xms1024m -Xmx1024m

## 新增诡异的例子
  [x] 被动使用字段,导致类没有初始化.
    
    @ classload.NotInitialization
     
  [x] Integer类Cache，以及反射修改导致的问题.
  
    @ jdkcode.IntegerCode
 ---
 
  **<font color=red>以下摘抄自 <<深入理解 JAVA虚拟机>></font>**
 ##  类加载
 **加载**
 > “加载”(Loading)阶段是“类加载”(Class Loading)过程的第一个阶段，在此阶段，虚拟机需要完成以下三件事情：

     1、 通过一个类的全限定名来获取定义此类的二进制字节流。
     2、 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构。
     3、 在Java堆中生成一个代表这个类的java.lang.Class对象，作为方法区这些数据的访问入口。
   
   加载阶段即可以使用系统提供的类加载器在完成，也可以由用户自定义的类加载器来完成。加载阶段与连接阶段的部分内容(如一部分字节码文件格式验证动作)是交叉进行的，加载阶段尚未完成，连接阶段可能已经开始。
   
 **验证**
 * x1: 文件格式验证
 > * 是否以魔数0xCAFEBABE开头。
 > * 主次版本是否在当前虚拟机处理范围之内。
 > * 常量池常量是否有不被支持的类型 (检查常量tag标志)
> * 指向常量的各种索引值重是否有指向不存在的常量或者不符合类型的常量
> * CONSTANT_Utf8_info型的常量中是否有不符合UTF8编码的数据。
> * ......
* x2: 元数据验证
> * 这个类是否有父类 (除了java.lang.Object之外，所有类都应该有父类)
> * .....

* x3: 字节码验证
> * 保证任意时刻操作数栈的数据类型与指令代码序列都能配合工作，例如不会出现在操作栈放置了int类型，使用时却按long类型加载本地变量
> * 保证跳转指令不会跳转到方法体以外的字节码指令上。
> * 保证方法体中的类型转换是有效的。

 * x4: 符号引用验证：
 > * 符号引用验证的目的是确保解析动作能正常执行，如果无法通过验证，则会抛出java.lang.IncompatibleClassChangeError异常的子类, 如java.lang.IllegalAccessError，java.lang.NoSuchFieldError,java.lang.NoSuchMethodError等
  符号引用非必须，所以在编译器反复验证过的情况下，可以使用 -Xverify:none来关闭以增加类加载的速度。

 **准备**
 准备阶段是正式分配内存并设置类变量初始值的阶段，这些变量将在方法区分配。
 此阶段只会对类变量进行内存分配，只对类变量进行设置默认值，类中赋予的值putstatic指令是程序编译后，存放在类构造器<clinit>()方法之中，赋值动作将在初始化阶段才会执行。
 
  
