package design.polymorphic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * author:ycc
 * @param <T>
 * 描述： 柔性多态， 在父接口中定义转发方法，子类实现转发，具体转发规则由子类决定。实现增加规则时不需要重新编译接口。
 */
public interface Polymorphic<T> {

//Polymorphic function definition

    T dispatch(int nID, T in) throws ReflectiveOperationException;

}

class Flexible<T> implements Polymorphic<T> {
    private T in;
    static class Nid {
        public static List<String> methods = new ArrayList<>();
        static String method1 = "method1";
        static String method2 = "method2";

        static {
            methods.add(method1);
            methods.add(method2);
        }
    }

    @Override
    public T dispatch(int nID, T in) throws ReflectiveOperationException {
        this.in = in;
        Method declaredMethod = this.getClass().getDeclaredMethod(Nid.methods.get(nID));
        return (T) declaredMethod.invoke(this);
    }

    public  T method1(){
        //handler
        return in;
    }


    public  T method2(){
//handler
        return in;
    }

    public static void main(String[] args)throws Exception {
        Polymorphic polymorphicNonT  = new Flexible();
        Float dispatch = (Float) polymorphicNonT.dispatch(1, 1.0f);
        System.out.println(dispatch);
        Polymorphic<FlexibleBean> polymorphicHasT  = new Flexible<>();
        FlexibleBean dispatch1 = polymorphicHasT.dispatch(0, new FlexibleBean());
        System.out.println(dispatch1);
    }

}

class FlexibleBean{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FlexibleBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}