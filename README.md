---
title: SimpleTuning
categories: java
tags: [java] 
description: Simple JVM  Tuning simulation,一些怪异的面试题，深入java虚拟机部分笔记以及书本部分资料摘抄。
---



[TOC]



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

# 记录书籍例子以及怪异的面试题

  **<font color=red>部分例子的代码见：[jvm_SimpleTuning_github_mysticalycc](https://github.com/yccjack/SimpleTuning )</font>**

## Integer类
  [x] Integer类Cache，以及反射修改导致的问题.

    @ jdkcode.IntegerCode
---


 ##  类加载
  **<font color=red>以下整理自 <<深入理解 JAVA虚拟机>></font>**



### **加载**
 > “加载”(Loading)阶段是“类加载”(Class Loading)过程的第一个阶段，在此阶段，虚拟机需要完成以下三件事情：

     1、 通过一个类的全限定名来获取定义此类的二进制字节流。
     2、 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构。
     3、 在Java堆中生成一个代表这个类的java.lang.Class对象，作为方法区这些数据的访问入口。

   加载阶段即可以使用系统提供的类加载器在完成，也可以由用户自定义的类加载器来完成。加载阶段与连接阶段的部分内容(如一部分字节码文件格式验证动作)是交叉进行的，加载阶段尚未完成，连接阶段可能已经开始。

 ### **验证**
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

 ### **准备**
 > *准备阶段是正式分配内存并设置类变量初始值的阶段，这些变量将在方法区分配。
 >  * 此阶段只会对类变量进行内存分配，只对类变量进行设置默认值，类中赋予的值putstatic指令是程序编译后，
> 存放在类构造器<clinit\>()方法之中，赋值动作将在初始化阶段才会执行。
> 见例子：classload.LoadClassInit

### **解析**
解析阶段是将常量池中的符号引用替换为直接引用的过程。**在进行解析之前需要对符号引用进行解析，不同虚拟机实现可以根据需要判断到底是在类被加载器加载的时候对常量池的符号引用进行解析（也就是初始化之前），还是等到一个符号引用被使用之前进行解析（也就是在初始化之后）。

到现在我们已经明白解析阶段的时机，那么还有一个问题是：如果一个符号引用进行多次解析请求，虚拟机中除了invokedynamic指令外，虚拟机可以对第一次解析的结果进行缓存（在运行时常量池中记录引用，并把常量标识为一解析状态），这样就避免了一个符号引用的多次解析。

解析动作主要针对的是类或者接口、字段、类方法、方法类型、方法句柄和调用点限定符7类符号引用。这里主要说明前四种的解析过程。

**类或者接口解析**

要把一个类或者接口的符号引用解析为直接引用，需要以下三个步骤：
> 1. 如果该符号引用不是一个数组类型，那么虚拟机将会把该符号代表的全限定名称传递给类加载器去加载这个类。这个过程由于涉及验证过程所以可能会触发其他相关类的加载
 > 2. 如果该符号引用是一个数组类型，并且该数组的元素类型是对象。我们知道符号引用是存在方法区的常量池中的，该符号引用的描述符会类似”[java/lang/Integer”的形式，将会按照上面的规则进行加载数组元素类型，如果描述符如前面假设的形式，需要加载的元素类型就是java.lang.Integer ,接着由虚拟机将会生成一个代表此数组对象的直接引用
 > 3. 如果上面的步骤都没有出现异常，那么该符号引用已经在虚拟机中产生了一个直接引用，但是在解析完成之前需要对符号引用进行验证，主要是确认当前调用这个符号引用的类是否具有访问权限，如果没有访问权限将抛出java.lang.IllegalAccess异常

**字段解析**

对字段的解析需要首先对其所属的类进行解析，因为字段是属于类的，只有在正确解析得到其类的正确的直接引用才能继续对字段的解析。对字段的解析主要包括以下几个步骤：
 > 1. 如果该字段符号引用就包含了简单名称和字段描述符都与目标相匹配的字段，则返回这个字段的直接引用，解析结束
  > 2. 否则，如果在该符号的类实现了接口，将会按照继承关系从下往上递归搜索各个接口和它的父接口，如果在接口中包含了简单名称和字段描述符都与目标相匹配的字段，那么久直接返回这个字段的直接引用，解析结束  
  > 3. 否则，如果该符号所在的类不是Object类的话，将会按照继承关系从下往上递归搜索其父类，如果在父类中包含了简单名称和字段描述符都相匹配的字段，那么直接返回这个字段的直接引用，解析结束
  > 4. 否则，解析失败，抛出java.lang.NoSuchFieldError异常
  > 5. ***见例子：classload.FileResolution***

如果最终返回了这个字段的直接引用，就进行权限验证，如果发现不具备对字段的访问权限，将抛出java.lang.IllegalAccessError异常

**类方法解析**

进行类方法的解析仍然需要先解析此类方法的类，在正确解析之后需要进行如下的步骤：
> 1. 类方法和接口方法的符号引用是分开的，所以如果在类方法表中发现class_index（类中方法的符号引用）的索引是一个接口，那么会抛出java.lang.IncompatibleClassChangeError的异常
 > 
 > 2. 如果class_index的索引确实是一个类，那么在该类中查找是否有简单名称和描述符都与目标字段相匹配的方法，如果有的话就返回这个方法的直接引用，查找结束
 > 
 > 3. 否则，在该类的父类中递归查找是否具有简单名称和描述符都与目标字段相匹配的字段，如果有，则直接返回这个字段的直接引用，查找结束
 > 
>  4. 否则，在这个类的接口以及它的父接口中递归查找，如果找到的话就说明这个方法是一个抽象类，查找结束，返回java.lang.AbstractMethodError异常
 > 
 > 5. 否则，查找失败，抛出java.lang.NoSuchMethodError异常

如果最终返回了直接引用，还需要对该符号引用进行权限验证，如果没有访问权限，就抛出java.lang.IllegalAccessError异常

**接口方法解析**

同类方法解析一样，也需要先解析出该方法的类或者接口的符号引用，如果解析成功，就进行下面的解析工作：
> 1. 如果在接口方法表中发现class_index的索引是一个类而不是一个接口，那么也会抛出java.lang.IncompatibleClassChangeError的异常
 > 
 > 2. 否则，在该接口方法的所属的接口中查找是否具有简单名称和描述符都与目标字段相匹配的方法，如果有的话就直接返回这个方法的直接引用。
 > 
 > 3. 否则，在该接口以及其父接口中查找，直到Object类，如果找到则直接返回这个方法的直接引用
 > 
 > 4. 否则，查找失败

接口的所有方法都是public，所以不存在访问权限问题。

### 初始化
初始化阶段是类加载过程的最后一步，这个阶段才开始真正的执行用户定义的Java程序。在准备阶段，变量已经赋过一次系统要求的初始值，而在初始化阶段，则需要为类变量(非final修饰的类变量)和其他变量赋值，其实就是执行类的<clinit>()方法。在Java语言体系中，<clinit>()是由编译器生成的，编译器在编译阶段会自动收集类中的所有类变量的赋值动作和静态语句块(static{})中的语句合并而成的，编译器收集的顺序是由语句的顺序决定的，静态语句块只能访问到定义在静态语句块之前的变量，定义在静态语句块之后的变量，可以赋值，但是不能访问。

<clinit\>()方法与类的构造方法不同，它不需要用户显示的调用，虚拟机会保证父类的<clinit\>()方法先于子类的<clinit\>()执行，java.lang.Object的<clinit>()方法是最先执行的。接口中不能使用用静态语句块，所以接口的<clinit>()只包含类变量，所以接口的<clinit>()方法执行时，不要求限制性父接口的<clinit>()方法。<clinit>()方法对于类和接口来说不是必须的，如果类或接口中没有定义类变量，也没有静态语句块，那么编译器将不为这个类或者接口生成<clinit>()方法，如果类或者接口中生成了<clinit>()方法，那么这个方法在执行过程中，虚拟机会保证在多线程环境下的线程安全问题。

　　虚拟机规范给了严格规定，有且只有以下几种情况必须立即对类进行初始化：

    1、遇到new、putstatic、getstatic及invokestatic这4条字节码指令时，如果类没有初始化，则立即进行初始化，这4个命令分别代表实例化一个类、设置&读取一个静态字段(没有被final修饰)、调用类的静态方法；
    2、使用java.lang.reflect包的方法对类进行反射调用的时候，如果类没有初始化；
    3、当初始化一个类的时候，发现其父类没有初始化；
    4、当虚拟机启动时，需用将执行启动的主类(有main()方法的那个类)进行初始化；
    5、当使用动态语言时，如果一个java.lang.invoke.MethodHandle实例最终的解析结果是REF_getStatic、REF_putStatic、REF_invokeStatic句柄时，并且这个句柄对应的类没有初始化。

[x] 被动使用字段,导致类没有初始化. 对于必须初始化的反例
 例子： ```@ classload.NotInitialization```

## 类与类加载器
> 对于任意一个类，都需要加载它得加载器和这个类本身一同确立其在Java虚拟机中得唯一性，对于类加载器，都拥有一个独立的类名称空间。
 两个类相同 包括代表类的Class对象的equals()方法，isAssignableFrom()方法，isInstance()方法返回结果，也包括使用instanceof关键字做对象所属关系判定等情况，如果未注意类加载器影响，在某些情况下可能会产生迷惑性结果。
>
 > 例子： classload.ClassLoadDoubleClass

 > 双亲委派
 > > 如果一个类加载器收到了类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，每一个层次的类加载器都是如此，因此所有的加载请求
 >  最终都应该传送到顶层的启动类加载器中，只有当父加载器反馈自己无法完成这个加载请求(它搜索范围中没有找到所需要的类)时，子加载器才会尝试加载
>
>类加载之间是组合关系，非继承关系。
>由于 类的相同需要与类加载绑定，所以使用双亲委派加载类可以保证rt.jar,bin/lib下面的类都是由系统自身的加载器加载，而不是用户自定义加载，导致多个相同得类存在。


## 虚拟机字节码执行引擎

### 运行时栈帧结构

> 栈帧（Stack Frame）是用于支持虚拟机进行方法调用和方法执行的数据结构。它是虚拟机运行时数据区中的虚拟机栈的栈元素。
>
> 栈帧存储了方法的局部变量表、操作数栈、动态连接和方法返回地址等信息。
>
> 每一个方法从调用开始至执行完成的过程，都对应着一个栈帧在虚拟机里面从入栈到出栈的过程。
>
> > 在编译程序代码的时候，栈帧中需要多大的局部变量表，多深的操作数栈都已经完全确定了。  因此一个栈帧需要分配多少内存，不会受到程序运行期变量数据的影响，而仅仅取决于具体的虚拟机实现。

![123](images/20180121103152636.png)

### 局部变量表

> 局部变量表（Local Variable Table）是一组变量值存储空间，用于存放方法参数和方法内部定义的局部变量。并且在Java编译为Class文件时，就已经确定了该方法所需要分配的局部变量表的最大容量。
>

局部变量表的容量以变量槽为最小单位，每个变量槽都可以存储32位长度的内存空间，例如boolean、byte、char、short、int、float、reference。

对于64位长度的数据类型（long，double），虚拟机会以高位对齐方式为其分配两个连续的Slot空间，也就是相当于把一次long和double数据类型读写分割成为两次32位读写。

> 在方法执行时，虚拟机使用局部变量表完成参数值到参数变量列表的传递过程的，如果执行的是实例方法，那局部变量表中第0位索引的Slot默认是用于传递方法所属对象实例的引用。（在方法中可以通过关键字this来访问到这个隐含的参数）。
  其余参数则按照参数表顺序排列，占用从1开始的局部变量Slot。
>
**Slot复用**

为了尽可能节省栈帧空间，局部变量表中的Slot是可以重用的，也就是说当PC计数器的指令指已经超出了某个变量的作用域（执行完毕），那这个变量对应的Slot就可以交给其他变量使用。

优点 ： 节省栈帧空间。

缺点 ： 影响到系统的垃圾收集行为。（如大方法占用较多的Slot，执行完该方法的作用域后没有对Slot赋值或者清空设置null值，垃圾回收器便不能及时的回收该内存。）

### 方法调用 
 方法调用不同于方法执行，方法调用阶段的唯一任务就是确定被调用方法的版本（即调用哪一个方法），暂时还不涉及方法内部的具体运行过程。Class文件的编译过程中不包括传统编译器中的连接步骤，一切方法调用在Class文件里面存储的都是符号引用，而不是方法在实际运行时内存布局中的入口地址（直接引用）。也就是需要在类加载阶段，甚至到运行期才能确定目标方法的直接引用。



**解析**

 如前所述，所有的方法调用中的目标方法在Class文件里面都是一个常量池中的符号引用，在类加载阶段，会将其中的一部分符号引用转化为直接引用，这种解析能成立的前提是：**方法在程序真正运行之前就有一个可确定的调用版本，并且这个方法的调用版本在运行期间是不可变的。**也就是说，调用目标在程序代码写好、编译器进行编译时就必须确定下来，这类方法的调用成为解析。

   JAVA中符号“编译器可知、运行期不可变”的方法包括：**静态方法、私有方法两大类。**前者与类型直接关联，后者在外部不可被访问，这就决定了他们都不可能通过继承或别的方式重写其版本。因此都适合在类的加载阶段进行解析。

   JAVA虚拟机里面提供了5条方法调用字节码指令。分别如下：

   **invokestatic:**调用静态方法

   **invokespecial:**调用实例构造器<init>方法、私有方法和父类方法（super(),super.method()）。

   **invokevirtual:**调用所有的虚方法(**静态方法、私有方法、实例构造器、父类方法、final方法都是非虚方法**)。

   **invokeinterface:**调用接口方法，会在运行时期再确定一个实现此接口的对象。

   **invokedynamic:**现在运行时期动态解析出调用点限定符所引用的方法，然后再执行该方法，在此之前的4条指令，分派逻辑都是固化在虚拟机里面的，而invokedynamic指令的分派逻辑是由用户所设定的引导方法决定的。

   只要能被invokestatic和invokespecial指令调用的方法都可以在解析阶段中确定唯一的调用版本，符合这个条件的有静态方法、私有方法、实例构造器、父类方法4类，它们在类加载阶段就会把符号引用解析为该方法的直接引用。这些方法称为非虚方法（还包括使用final修饰的方法，虽然final方法使用invokevirtual指令调用，因为final方法注定不会被重写，也就是无法被覆盖，也就无需对其进行多态选择）。

   **解析调用一定是一个静态的过程，在编译期间就可以完全确定，在类装载的解析阶段就会把涉及的符号引用全部转化为可确定的直接引用，不会延迟到运行期去完成。**而**分派调用**可能是静态的也可能是动态的，根据分派一句的宗量数可分为单分派和多分派。因此分派可分为：静态单分派、静态多分派、动态单分派、动态多分派。



 **分派**

**1.静态分派（方法重载）：**

   先看一段代码：

```
 1 public class StaticDispatch {
 2     static abstract class Human{
 3 
 4     }
 5     static class Man extends Human{
 6 
 7     }
 8     static class Woman extends  Human{
 9 
10     }
11     @Test
12     public void test(){
13         Human man = new Man();
14         Human woman = new Woman();
15         StaticDispatch sr = new StaticDispatch();
16         sr.sayHello(man);
17         sr.sayHello(woman);
18 
19     }
20 
21 
22     public void sayHello(Human guy){
23         System.out.println("Hello guy");
24     }
25     public void sayHello(Man guy){
26         System.out.println("Hello man");
27     }
28     public void sayHello(Woman guy){
29         System.out.println("Hello woman");
30     }
31 }
```

   运行结果为：

```
Hello guy
Hello guy
```

​    要解释上面的现象，先要说明几个概念，看如下代码。

   **Human man = new Man();**

   上面一行代码中，Human成为变量man的**静态类型**，或者叫做外观类型，后面的Man则称为变量的**实际类型**，静态类型和实际类型在程序中都可以发生一些变化，区别是静态类型的变化仅仅在使用时发生（比如强制类型转换），**变量本身的静态类型不会改变，并且最终的静态类型在编译器就是可知的；**而**实际类型变化的结果在运行期才可以确定，编译器在编译程序的时候并不知道一个对象的实际类型是什么。**

   比如如下代码：

  //实际类型变化

  Human man = new Man();

  Human woman = new Woman();

  //通过强转实现静态类型变化(变量本身静态类型不变)

  sr.sayHello((Man)man);

  sr.sayHello((Woman)woman);

 

​    **虚拟机（编译器）在确定重载函数版本时是通过参数的静态类型而不是实际类型作为判定依据。因此，在编译阶段，编译器就可以根据静态类型确定使用哪个重载的版本。**

> 见例子： ```@ classload.Overload```

**2.动态分派（方法重写Override）：**

   为了说明动态分派的概念，先看一段代码：

```
 1 public class DynamicDispatch{
 2     static abstract  class Human{
 3         protected abstract void sayHello();
 4     }
 5     static class Man extends Human{
 6         @Override
 7         protected void sayHello(){
 8          System.out.println("man say hello");
 9         }
10     }
11     static class Woman extends Human{
12         @Override
13         protected void sayHello(){
14             System.out.println("woman say hello");
15         }
16     }
17 
18 19     public static void man(String[] args){
20         Human man = new Man();
21         Human woman = new Woman();
22         man.sayHello();
23         woman.sayHello();
24         man = new Woman();
25         man.sayHello();
26 
27     }
28 
29 }
```



   输出结果为：

```
man say hello
woman say hello
woman say hello
```

   熟悉多态的人对上面的结果不会感到惊讶。下面使用javap命令输出这段代码的字节码。

![img](https://images2015.cnblogs.com/blog/592743/201603/592743-20160322110744917-900187291.png)

![img](https://images2015.cnblogs.com/blog/592743/201603/592743-20160322110827979-422248272.png)

​    如上所示，方法的调用指令都使用了invokevirtual指令，invokevirtual指令的运行时解析过程大致分为以下几个步骤。

​    1）找到操作数栈顶的第一个元素(对象引用)所指向的对象的**实际类型**，记作C；

​    2）如果在类型C中找到与常量中的描述符和简单名称都相符的方法，则进行访问权限校验，如果通过则返回这个方法的直接引用，查找过程结束；如果不通过，则返回java.lang.IllegalAccessError。

   3）否则，按照继承关系从下往上依次对C的各个父类进行第2步的搜索和验证。

   4）如果始终没有找到合适的方法，则抛出java.lang.AbstractMethodError异常。

   

   **由于invokevirtual指令执行的第一步就是在运行期确定接收者的实际类型，这又是java语言中方法重写产生多态的本质。**

 

**3.单分派与多分派**

   方法的接收者和方法的参数统称为方法的宗量。根据分派基于多少种宗量，可以将分派划分为单分派和多分派。**单分派是根据一个宗量对目标方法进行选择，多分派则是根据多于一个宗量对目标方法进行选择。**

```
 1 import org.junit.Test;
 2 
 3 /**
 4  * Created by chen on 2016/3/23.
 5  */
 6 public class Dispatch {
 7     static class QQ{
 8 
 9     }
10     static class _360{
11 
12     }
13 
14     public static class Father{
15         public void hardChoice(QQ  arg){
16             System.out.println("father choose qq");
17         }
18         public void hardChoice(_360  arg){
19             System.out.println("father choose 360");
20         }
21     }
22     public static class Son extends Father{
23         public void hardChoice(QQ  arg){
24             System.out.println("son choose qq");
25         }
26         public void hardChoice(_360  arg){
27             System.out.println("son choose 360");
28         }
29     }
30     @Test
31     public void test(){
32         Father father = new Father();
33         Father son = new Son();
34         father.hardChoice(new _360());
35         son.hardChoice(new QQ());
36     }
37 }
```

   运行结果：

```
father choose 360
son choose qq
```

   上述有关于hardChoice方法的两次调用，涉及了静态分派和动态分派的过程。

   首先看看编译阶段编译器的选择，也就是静态分派的过程(关于重载)。**此时选择目标方法的依据有两点：一是静态类型是Father还是Son，而是方法参数是QQ还是_360。**此处选择结果最终的产物是产生了两条invokevirtual指令，两条指令的参数分别是指向Father.hardChoice(_360)和Father.hardChoice(QQ)方法的符号引用。因为是根据两个宗量进行分派，所以java语言的静态分派属于多分派类型。

   再看看运行阶段虚拟机的选择，也就是动态分派的过程（关于重写），在执行“son.hardChoice(new QQ());”这句代码时，更准确的说，是在执行invokevirtual指令时，由于编译器已经确定了目标方法的签名必须是hardChoice(QQ)，虚拟机此时不会关心传过来的参数类型，也就是此时传过来的实际类型、静态类型都不会对产生任何影响。**唯一可以对虚拟机的选择产生影响的就是此方法的接收者的实际类型是Father还是Son**。因为只有一个宗量作为依据，所以java语言的动态分派属于单分派。

**4、虚拟机动态分派的实现**

   由于动态分派是非常频繁的动作，而且动态分派的方法版本选择过程需要运行时在类的方法元数据中搜索合适的目标方法，因此在虚拟机的实际实现中基于性能的考虑，大部分实现都不会真正的进行如此频繁的搜索。面对这种情况，最常用的“稳定优化”手段就是**为类在方法区中建立一个虚方法表**（vtable，熟悉C++的肯定很熟悉。于此对应的，在invokeinterface执行时也会用到接口方法表---itable），**使用虚方法表索引来代替元数据查找以提高性能。**具体如下图所示：

​    ![img](https://images2015.cnblogs.com/blog/592743/201603/592743-20160322113550886-2079596000.png)

   虚方法表中存放着各个方法的实际入口地址，如果某个方法在子类中没有被重写，那子类的虚方法表里面的地址入口和父类相同方法的入口地址是一致的，都指向父类的实现入口。如果子类重写了这个方法，子类方法表中的地址将会替换为指向子类实现版本的入口地址。如上图所示，Son重写了来自Father的全部方法，因此Son的方法表没有指向Father类型数据的箭头。但是Son和Father都没有重写来自Object的方法，所以他们的方法表中所有从Object继承来的方法都指向了Object的数据类型。

   为了程序实现上的方便**，具有相同签名的方法，在父类、子类的虚方法表中都应当具有一样的索引号**，这样当类型变换时，仅需要变更查找的方法表，就可以从不同的虚方法表中按照索引转换出所需要的方法入口地址。

   **方法表一般在类加载阶段的连接阶段进行初始化**，准备了类变量初始值之后，虚拟机会把该类的方法表也初始化完毕。

### 基于栈的字节码解释执行引擎

Java编译器输出的指令流，基本上（是因为部分字节码指令会带有参数，而纯粹基于栈的指令集架构中应当全部都是零地址指令，也就是都不存在显式的参数。Java这样实现主要是考虑了代码的可校验性。）是一种基于栈的指令集架构（Instruction Set Arhitecture，ISA），指令流中的指令大部分都是零地址指令，他们依赖操作数栈进行工作。与之相对的另外一套常用的指令集架构是基于寄存器的指令集，最典型的就是x86的二地址指令集，说的通俗一些，就是现在我们主流PC机中直接支持的指令集架构，这些指令依赖寄存器进行工作。那么，基于栈的指令集与基于寄存器的指令集这两者之间有什么不同呢？
举个最简单的例子，分别使用这两种指令集计算“1+1”的结果，基于栈的指令集会是这样子的：

> iconst_1
>
> iconst_1
>
> iadd
>
> istore_0

两条iconst_1指令连续把两个常量1压入栈后，iadd指令把栈顶的两个值出栈、相加，然后把结果放回栈顶，最后istore_0把栈顶的值放到局部变量表的第0个Slot中。
如果基于寄存器，那程序可能会是这个样子：

> mov eax,1
>
> add eax,1

> mov指令把EAX寄存器的值设为1，然后add指令再把这个值加1，结果就保存在EAX寄存器里面。
> 了解了基于栈的指令集与基于寄存器的指令集的区别后，读者可能会有进一步的疑问，这两套指令集谁更好一些呢？
> 应该这么说，既然两套指令集会同时并存和发展，那肯定是各有优势的，如果有一套指令集全面优于另外一套的话，就不会存在选择的问题了。
> 基于栈的指令集主要优点就是可移植，寄存器由硬件直接提供，程序直接依赖这些硬件寄存器直接提供，程序直接依赖这些硬件寄存器则不可避免地要受到硬件的约束。例如，现在32位80x86体系的处理器中提供了8个32位的寄存器，而ARM体系的CPU（在当前的手机、PDA中相当流行的一种处理器）则提供了16个32位的通用寄存器。如果使用栈架构的指令集，用户程序不会直接使用这些寄存器，就可以由虚拟机实现来自行决定把一些访问最频繁的数据（程序计数器、栈顶缓存等）放到寄存器中以获取尽量好的性能，这样实现起来也更加简单一些。栈架构的指令集还有一些其他的优点，如代码相对更加紧凑（字节码中每个字节就对应一条指令，而多地址指令集中还需要存放参数）、编译器实现更加简单（不需要考虑空间分配的问题，所需空间都在栈上操作）等。
> 栈架构指令集的主要缺点是执行速度相对来说会稍慢一些。所有主流物理机的指令集都是寄存器架构也从侧面印证了这一点。
> 虽然栈架构指令集的代码非常紧凑，但是完成相同功能所需的指令数量一般会比寄存器架构多，因为出栈、入栈操作本身就产生了相当多的指令数量。更重要的是，栈实现在内存之中，频繁的栈访问也就意味着频繁的内存访问，相对于处理器来说，内存始终是执行速度的瓶颈。尽管虚拟机可以采取栈顶缓存的手段，把最常用的操作映射到寄存器中避免直接内存访问，但这也只能是优化措施而不是解决本质问题的方法。由于指令数量和内存访问的原因，所以导致了栈架构指令集的执行速度会相对较慢。

# 基于栈的解释器执行过程

初步的理论知识已经讲解过了，本节准备了一段Java代码，看看在虚拟机中实际是如何执行的。下面准备了四则运算的例子，请看下面代码。

```java
public class CalcTest {
	public int calc() {
		int a = 100;
		int b = 200;
		int c = 300;
		return (a+b)*c;
	}
}
```

从Java语言的角度来看，这段代码没有任何解释的必要，可以直接使用javap命令看看他的字节码指令，如下所示。

![img](https://img-blog.csdnimg.cn/20190530164012303.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

javap提示这段代码需要深度为2的操作数栈和4个Slot的局部变量空间，根据这些信息画了下面共7张图，用他们来描述上面执行过程中的代码、操作数栈和局部变量表的变化情况。

![img](https://img-blog.csdnimg.cn/20190530164136230.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190530164150140.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190530164202603.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190530164217361.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190530164226540.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190530164238642.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

![img](https://img-blog.csdnimg.cn/20190530164246561.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2VuX2pva2Vy,size_16,color_FFFFFF,t_70)

上面的执行过程仅仅是一种概念模型，虚拟机最终会对执行过程做一些优化来提高性能，实际运行过程不一定完全符合概念模型的描述......更准确地说，实际情况会和上面的字节码进行优化，例如，在HotSpot虚拟机中，有很多以“fast_”开头的非标准字节码指令用于合并、替换输入的字节码以提升解释执行性能，而即时编译器的优化手段更加花样繁多。
不过，我们从这段程序的执行中也可以看出栈结构指令集的一般运行过程，整个运算过程的中间变量都以操作数栈的出栈、入栈为信息交换途径，符合我们在前面分析的特点。


### 字节码生成与动态代理

**动态代理** -> proxy.DynamicProxy