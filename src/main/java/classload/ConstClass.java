package classload;

/**
 * @author ycc
 * @time 21:20
 */
public class ConstClass {
    static {
        System.out.println("ConstClass init!");
    }

    public static final String HELLOWORD ="hello world!";
}
