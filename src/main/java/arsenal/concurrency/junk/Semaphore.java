package arsenal.concurrency.junk;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {

	private Lock semlock = new ReentrantLock();

	private Condition isEmpty = semlock.newCondition();

	//accessed only when holding semlock
	private int count;

	public Semaphore(int count) {
		this.count = count;
	}

	public void acquire() throws InterruptedException{
		semlock.lock();
		try{
			while(count<=0) {
                System.out.println("Going to wait : " + Thread.currentThread().getName());
				isEmpty.await();
			}
            System.out.println("Decrementing count : " + count + " : " + Thread.currentThread().getName());
			count--;
		}finally{
			semlock.unlock();
		}
	}

	public void release() {
		semlock.lock();
		try{
            System.out.println("Releasing sem");
			count++;
			isEmpty.signal();
		}finally {
			semlock.unlock();
		}
	}



}