package jvm;

/**
 * @author ycc
 * @time 15:57
 * 证明语句乱序的例子(语句乱序-->指令乱序)
 * 假设不存在语句重排序(指令重排序)，那么 就不会出现 x=0,y=0的情况，也就是循环不会退出。
 * 经测试 本人机子第  第290042次( x= 0 y = 0)
 */
public class OutOfOrder {

    private static int a,b;
    private static int x,y;
    public static void main(String[] args) throws InterruptedException {
        int i=0;
        while (true){
            i++;
            a=0;b=0;
            x=0;y=0;
            Thread t1= new Thread(()->{
                a=1;
                x=b;
            });
            Thread t2= new Thread(()->{
                b=1;
                y=a;
            });
            t1.start();t2.start();
            t1.join();t2.join();
            String result = "第"+i+"次( x= "+x+"， y = "+y+")";
            if(x==0&&y==0){
                System.out.println(result);
                break;
            }
        }
    }
}
