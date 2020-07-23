package classload;

/**
 * @author ycc
 * @time 21:15
 */
public class NotInitialization {
    public static void main(String[] args) {
        testSupperInitSubNotInit();
        testArrayInit();
        testConstInit();
    }

    /**
     * 对于静态字段，只有直接定义这个字段的类才会被初始化。因此父类被初始化，子类没有被初始化
     */
    public static void testSupperInitSubNotInit(){
        System.out.println(SubClass.value);


    }

    /**
     * vm = -XX:+TraceClassLoading
     * 被动使用字段，通过使用数据来定义应用类，不i会触发此类的初始化
     */
    public static void testArrayInit(){
        SuperClass[] sca = new SuperClass[10];
    }

    /**
     * 常量在编译简短回存入调用类的常量池中，本质上病没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化。
     * 对比 testSupperInitSubNotInit
     * @see NotInitialization#testSupperInitSubNotInit()
     */
    public static void testConstInit(){
        System.out.println(ConstClass.HELLOWORD);
    }
}
