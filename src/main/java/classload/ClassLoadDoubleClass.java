package classload;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author ycc
 * @time 20:44
 * 不同类加载器加载同一个类，属于不同得类
 */
public class ClassLoadDoubleClass {
    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = new ClassLoader(){
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                String fileName = name.substring(name.lastIndexOf(".")+1)+".class";
                InputStream resourceAsStream = getClass().getResourceAsStream(fileName);
                if (resourceAsStream==null){
                    return super.loadClass(name);
                }
                try {
                    byte[] bytes = new byte[resourceAsStream.available()];
                    resourceAsStream.read(bytes);
                    return defineClass(name,bytes,0,bytes.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };
        Object o = classLoader.loadClass("classload.ClassLoadDoubleClass").newInstance();
        System.out.println(o.getClass());
        //属于不同类
        System.out.println(o instanceof classload.ClassLoadDoubleClass);
    }
}
