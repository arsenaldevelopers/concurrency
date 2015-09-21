package arsenal.concurrency.junk;

public class ApplicationSemaphore {
	public static void main(String[] args) throws InterruptedException{
		final Semaphore sem = new Semaphore(5);

		for (int i = 0 ; i < 10; i++) {
		    new Thread(new Runnable() {
                public void run() {
                    try {
                        sem.acquire();
                        Thread.sleep(2000);
                        sem.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
		}

        System.out.println("Going to sleep for 3 seconds : " + Thread.currentThread().getName());
        Thread.sleep(5000);

	}
}