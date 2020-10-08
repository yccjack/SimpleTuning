package classload;

/**
 * @author ycc
 * @time 21:15
 * 初始化
 * 主要通过执行类构造器的 <client> 方法为类进行初始化 <client> 方法是在编译阶段由编译器自动收集类中静态语句块和变量的赋值操作组成的 JVM 规定,只有在父类的 <client> 方法都执行成功后,子类中的 <client> 方法才可以被执行 在一个类中既没有静态变量赋值操作也没有静态语句块时,编译器不会为该类生成 <client> 方法
 *
 *  在发生以下几种情况时,JVM 不会执行类的初始化流程
 *
 * - 常量在编译时会将其常量值存入使用该常量的类的常量池中,该过程不需要调用常量所在的类,因此不会触发该常量类的初始化
 * - 在子类引用父类的静态字段时,不会触发子类的初始化,只会触发父类的初始化
 * - 定义对象数组,不会触发该类的初始化
 * - 在使用类名获取 Class 对象时不会触发类的初始化
 * - 在使用 Class.forName 加载指定的类时,可以通过 initialize 参数设置是否需要对类进行初始化
 * - 在使用 ClassLoader 默认的 loadClass 方法加载类时不会触发该类的初始化
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
