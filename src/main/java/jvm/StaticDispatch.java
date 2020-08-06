package jvm;

/**
 * @author ycc
 * @time 21:03
 * 静态分派
 */
public class StaticDispatch {
    static abstract class Human{

    }
    static class Man extends Human{

    }
    static class Woman extends Human{

    }
    public void sayHello(Human human){
        System.out.println("say hello");
    }

    public void sayHello(Man man){
        System.out.println("say man");
    }
    public void sayHello(Woman woman){
        System.out.println("say woman");
    }

    public static void main(String[] args) {
        Human man = new Man();
        Human woman = new Woman();
        StaticDispatch staticDispatch = new StaticDispatch();
        staticDispatch.sayHello(man);
        staticDispatch.sayHello(woman);
    }
}
