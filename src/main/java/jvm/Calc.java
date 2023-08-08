package jvm;

/**
 * @author ycc
 * @time 22:03
 */
public class Calc {

    public static synchronized int calc() {
        int a = 100;
        int b = 200;
        int c = 300;
        return (a + b) * c;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            calc();
        }
    }
}
