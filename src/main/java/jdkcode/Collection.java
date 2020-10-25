package jdkcode;

import java.util.Arrays;
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
        ints2.add(6);

    }
}
