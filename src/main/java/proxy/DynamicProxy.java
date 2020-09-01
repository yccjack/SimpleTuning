package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ycc
 * @time 22:05
 */
public class DynamicProxy {

    interface IHello{
        void hello();
    }

    static class Hello implements IHello{

        @Override
        public void hello() {
            System.out.println("hello");
        }
    }

    static class DynamicProxyObject implements InvocationHandler{
        Object originalObj;

        Object bind(Object originalObj){
            this.originalObj = originalObj;
            return Proxy.newProxyInstance(originalObj.getClass().getClassLoader(),originalObj.getClass().getInterfaces(),this);
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("welcome proxy");
            return method.invoke(originalObj,args);
        }
    }

    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles",true);
        IHello hello = (IHello) new DynamicProxyObject().bind(new Hello());

        hello.hello();
    }
}
