package jmc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JmcTest {

    public static void main(String[] args) {
        testMap();
    }

    /**
     *      jvm 参数：-Xmx2048m -Xms512m  -XX:+HeapDumpOnOutOfMemoryError   -XX:HeapDumpPath=file.hprof
     *      -XX:StartFlightRecording=filename=myrecording.jfr
     *      打开JVISUALVM 可以看到JDK的自调整(没有配置其他参数的情况下)
     */
    public static void testMap(){
        Map<Integer, HashSet<String>> masterMap = new HashMap<>(100000);
        for (int i = 0; i < 100000; i++) {
            System.out.println(i);
            masterMap.computeIfAbsent(i, HashSet::new).add("test"+i);
        }
    }
}
