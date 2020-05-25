# SimpleTuning
Simple JVM  Tuning simulation

# 运行
 * 设置参数 有一点需要注意的是，如果-Xms和-Xmx没有被设定成相同的值，而且-Xmn被使用了，当调整Java堆的大小的时候，
 * 不会调整young代的空间大小，young代的空间大小会保持恒定。因此，-Xmn应该在-Xms和-Xmx设定成相同的时候才指定。
 *
 *  vm_1 : 默认：-Xms:默认为物理内存的1/64 -Xmx:默认为物理内存的1/4或者1G,因为存在堆空间扩容，第一次运行的时候会执行多次FULL GC
 *  vm_2 : -Xms750m -Xms2048m -Xmx2048m
 *  vm_3 : -Xms1024m -Xms2048m -Xmx2048m
 *  vm_4 : -Xms1024m -Xms3096m -Xmx3096m
 *  vm_5 : -Xms250m -Xms1024m -Xmx1024m
 
