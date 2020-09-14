package load;

import classload.FileResolution;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author ycc
 * @time 21:18
 */
public class LoadClass {


    public static void main(String[] args) throws Exception{
        FileResolution o = LoadClass.loadClass("file:///D:/code/load/", FileResolution.class);
    }
    public static  <T>  T loadClass(String path,Class<?> clazz) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        URL url = new URL(path);
        URLClassLoader classLoaderLeft = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
        Class<?> leftClass = classLoaderLeft.loadClass(clazz.getName());
        return (T) leftClass.newInstance();
    }
}
