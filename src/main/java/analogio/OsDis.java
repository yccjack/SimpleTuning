package analogio;

/**
 * @author ycc
 * @time 20:10
 */
public class OsDis {
    public static class IOPro{
        protected SimulationFd fd;

    }

    private static class kernel{
        private KernelTCB tcb;
        private Calculation calculation;

    }
}

class SimulationFd{
    protected int numbering;
    protected byte[] buffer;

}

class KernelTCB{

}
class Calculation{

}



