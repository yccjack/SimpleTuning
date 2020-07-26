package jdkcode;

/**
 * @author ycc
 * @time 9:27
 * 因为static LoadClassInit loadClassInit 为类变量，类型为引用，所有非静态代码块会优先得到执行
 *  静态变量在两个代码块之下，按照程序顺序执行，前两个代码块会把静态变量 static修饰的打印出来初始值，但是final static变量属于常量，会在类加载是赋予指定的值。
 *  引用readme一句话： 如果类字段的字段属性表存在ConstantValue属性(final static修饰的变量)，那么准备阶段变量就会被初始化为ConstantValue属性所指定的值，
 */
public class LoadClassInit {

    public static LoadClassInit loadClassInit = new LoadClassInit();
     {
         System.out.println("非静态代码块执行结果");
        System.out.println(loadClassInit.staticValue);
        System.out.println(loadClassInit.finalStaticValue);
    }

    static {
        System.out.println("静态变量之前的静态代码块调用静态引用的值结果：");
        System.out.println(loadClassInit.staticValue);
        System.out.println(loadClassInit.finalStaticValue);
    }
    public static int staticValue = 123;

    public final static int finalStaticValue = 456;

    static {
        System.out.println("静态变量之后静态代码块调用静态变量和常量结果：");
        System.out.println(staticValue);
        System.out.println(finalStaticValue);
    }


    public LoadClassInit(){
        System.out.println("构造方法打印结果：");
        System.out.println(staticValue);
        System.out.println(finalStaticValue);
    }


    public static void main(String[] args) {
        System.out.println("main方法打印结果：");
        System.out.println(LoadClassInit.staticValue);
        System.out.println(LoadClassInit.finalStaticValue);

    }
}
