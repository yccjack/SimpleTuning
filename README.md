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





 
  
