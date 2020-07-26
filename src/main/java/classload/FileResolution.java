package classload;

/**
 * @author ycc
 * @time 14:54
 */
public class FileResolution {
        interface Interface0{
            int A = 0;
        }
        interface Interface1 extends Interface0{
            int A = 1;
        }
        interface Interface2{
            int A = 2;
        }
        static class Parents implements Interface1{
            public static int A = 3;
        }
        static class Sub extends Parents implements Interface2{
            public static  int A = 4; //此行如果注释掉则会拒绝编译
        }
        public static void main(String args[]){
            System.out.println(Sub.A);
        }

}
