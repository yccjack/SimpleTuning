package jdkcode;

import java.lang.reflect.Field;

/**
 * 使用交换方法，使的a ,b的值交换过来，并正确打印
 * @author MysticalYcc
 * @date 14:37
 */
public class IntegerCode {

    public static void main(String[] args) throws Exception {
        Integer a = 1;
        Integer b = 2;
        System.out.println("交换前a= "+a+",b= "+b);
        swap(a,b);
        System.out.println("交换后a= "+a+",b= "+b);
    }

    private static void swap(Integer a, Integer b) throws Exception {
        Class<? extends Integer> aClass = a.getClass();
        Field value = aClass.getDeclaredField("value");
        int temp = a;
        value.setAccessible(true);
        value.set(a,b);
        value.set(b,temp);

        System.out.println("交换时a= "+a+",b= "+b);
    }
}
