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

  [x] 被动使用字段,导致类没有初始化;
  
  [x] Integer类Cache，以及反射修改导致的问题
  
 
