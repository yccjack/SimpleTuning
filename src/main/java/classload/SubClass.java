package classload;

/**
 * @author ycc
 * @time 21:13
 */
public class SubClass extends SuperClass{
    static {
        System.out.println("SubClass init!");
    }
}
