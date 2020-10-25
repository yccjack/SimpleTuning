package jdkcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ycc
 * @time 14:49
 * asList接受的参数是一个泛型的变长参数，我们知道基本数据类型是无法发型化的，也就是说8个基本类型是无法作为asList的参数的，
 * 要想作为泛型参数就必须使用其所对应的包装类型。但是这个这个实例中为什么没有出错呢？因为该实例是将int 类型的数组当做其参数，而在Java中数组是一个对象，它是可以泛型化的。
 * 所以该例子是不会产生错误的。既然例子是将整个int 类型的数组当做泛型参数，那么经过asList转换就只有一个int 的列表了
 */
public class Collection {
    public static void main(String[] args) {
        int[] ints = {1,2,3,4};
        List ints1 = Arrays.asList(ints);
        System.out.println(ints1.size());

        Integer[] integers = {1,2,3,4};
        List ints2 = Arrays.asList(integers);
        System.out.println(ints2.size());

        //这里会报错 UnsupportedOperationException ，因为aslist生成的List不是我们熟悉的ArrayList，。而是Arrays的内部类，它的add、remove等改变list结果的方法从AbstractList父类继承过来
        //直接抛出UnsupportedOperationException异常. 不可改变这个数据的结构。
       // ints2.add(6);
        subList();

    }

    /**
     * subList只返回原List的一个视图，操作此视图依然是在操作原来的List， subList会将this 传入到构造方法中生成一个视图记录SubList。
     * SubList不允许原List做修改，这样会触发SubList的fast-fail 机制。
     * 针对上面的问题 ， 可以对list1设置为只读状态。
     * SubList用于修改集合局部数据，例如 list1.subList(100, 200).clear();
     */
    public static void subList(){
        List<Integer> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);

        //通过构造函数新建一个包含list1的列表 list2
        List<Integer> list2 = new ArrayList<>(list1);

        //通过subList生成一个与list1一样的列表 list3
        List<Integer> list3 = list1.subList(0, list1.size());

        //修改list3
        list3.add(3);

        System.out.println("list1 == list2：" + list1.equals(list2));
        System.out.println("list1 == list3：" + list1.equals(list3));

        // SubList不允许原List做修改，这样会触发SubList的fast-fail 机制。
        list1.add(3);

        System.out.println("list1'size：" + list1.size());
        //此处调用了SubList的size方法， 先执行了 checkForComodification()，
        System.out.println("list3'size：" + list3.size());

        //针对上面的问题 ， 可以对list1设置为只读状态
        list1 = Collections.unmodifiableList(list1);


    }
}
