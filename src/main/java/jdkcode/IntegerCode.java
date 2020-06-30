package jdkcode;

import java.lang.reflect.Field;

/**
 * 使用交换方法，使的a ,b的值交换过来，打印出交换后的值
 * @author MysticalYcc
 * @date 14:37
 */
public class IntegerCode {

    public static void main(String[] args) throws Exception {
        Integer a = 128;
        Integer b = 129;
        System.out.println("交换前a= "+a+",b= "+b);
        swap4(a,b);
        System.out.println("交换后a= "+a+",b= "+b);
    }

    /**
     * 此方法无法生效，Integer是final类；
     * @param a
     * @param b
     * @throws Exception
     */
    private static void swap(Integer a, Integer b) throws Exception{
        int temp = a;
        a=b;
        b=temp;
    }

    /**
     * 此方法依然无法生效，在 [-128,127] 区间的Integer返回的都是同一个对象，所以修改后 得出的结果会是  2， 2
     * @param a
     * @param b
     * @throws Exception
     */
    private static void swap2(Integer a, Integer b) throws Exception {
        Class<? extends Integer> aClass = a.getClass();
        Field value = aClass.getDeclaredField("value");
        int temp = a;
        value.setAccessible(true);
        value.set(a,b);
        value.set(b,temp);
    }

    /**
     * 此方法可行
     * @param a
     * @param b
     * @throws Exception
     */
    private static void swap3(Integer a, Integer b) throws Exception {
        Class<? extends Integer> aClass = a.getClass();
        Field value = aClass.getDeclaredField("value");
        int temp = a;
        value.setAccessible(true);
        value.setInt(a,b);
        value.setInt(b,temp);
    }

    private static void swap4(Integer a, Integer b) throws Exception {
        int temp = a;
        a=b;
        b=temp;
        System.out.println("交换后a= "+a+",b= "+b);
        System.exit(0);
    }

}
