package arsenal.concurrency.junk;


public class Application {

	public static void main(String[] args) {

        int COUNT_BITS = Integer.SIZE - 3;
        System.out.println("count_bits : " + COUNT_BITS);
        int capacity = 1 << COUNT_BITS;
        System.out.println("capacity : " + capacity);

        // runState is stored in the high-order bits
        int RUNNING    = -1 << COUNT_BITS;
        int SHUTDOWN   =  0 << COUNT_BITS;
        int STOP       =  1 << COUNT_BITS;
        int TIDYING    =  2 << COUNT_BITS;
        int TERMINATED =  3 << COUNT_BITS;

        System.out.println("-1 : " + Integer.toBinaryString(-1));
        System.out.println("Running : " + Integer.toBinaryString(RUNNING));
        System.out.println("ShutDown : " + Integer.toBinaryString(SHUTDOWN));
        System.out.println("Stop : " + Integer.toBinaryString(STOP));
        System.out.println("TIDYING : " +  Integer.toBinaryString(TIDYING));
        System.out.println("TERMINATED : " + Integer.toBinaryString(TERMINATED));

	}


    private static void temp() throws InterruptedException{
        ExecutorService service = new ExecutorService();
        service.initialize();
        for(int i = 0 ; i < 100; i++) {
            Runnable work = new Runnable() {
                public void run() {
                    System.out.println("Task getting done by worker " + Thread.currentThread().getName() ) ;
                }
            };

            service.execute(work);
        }
    }
}