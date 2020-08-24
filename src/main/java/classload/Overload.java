package classload;

import java.io.Serializable;

/**
 * @author ycc
 * @time 20:58
 * 运行后以此注释调打印结果所调用的方法，可以观察到结果选择的变化，最终会确定一个 “更合适的版本” 来执行
 */
public class Overload {
    public static void sayHello(Object acg){
        System.out.println("hello Object");
    }
    public static void sayHello(int acg){
        System.out.println("hello int");
    }
    public static void sayHello(long acg){
        System.out.println("hello long");
    }
    public static void sayHello(Character acg){
        System.out.println("hello Character");
    }
    public static void sayHello(char acg){
        System.out.println("hello char");
    }
    public static void sayHello(char... acg){
        System.out.println("hello char...");
    }
    public static void sayHello(Serializable acg){
        System.out.println("hello Serializable");
    }

    public static void main(String[] args) {
        sayHello('a');
    }
}
