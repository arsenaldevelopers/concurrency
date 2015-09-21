package arsenal.concurrency.junk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutorService {

	Lock poolLock = new ReentrantLock();
	Condition poolEmpty = poolLock.newCondition();
	Condition poolFull = poolLock.newCondition();

	//Gaurded by lock
    BlockingQueue<Worker> pool = new LinkedBlockingQueue<Worker>();


    public void  execute(Runnable runnable) throws InterruptedException {
        Worker worker = pool.take();
        worker.setTask(runnable);
    }


    /**
     * Initializes thread pool
     */
    public void initialize() {
        for(int i = 0; i < 10; i++) {
        	pool.add(workerFactory("Worker " + i));
        }

    }


    //Thread worker that executing task. Each worker is aware
    // of it thread stack/context it will use to execute the task
    private class Worker implements Runnable {

        Thread workerThread;
        Worker() {
            workerThread = new Thread(this);
        }

        public void setWorkerName(String name){
        	workerThread.setName(name);
        }

        public void startWorker() {
            this.workerThread.start();
        }

        //should this worker execute the task or not
        volatile boolean state;

        Lock lock = new ReentrantLock();
        Condition waitForTask = lock.newCondition();
        //Gaurded by lock
        Runnable task;

        public void setTask(Runnable newTask) {
        	try{
        		lock.lock();
        		this.task = newTask;
        		state = true;
        		waitForTask.signal();
        	}finally{
        		lock.unlock();	
        	}
        } 


        public void run() {

            try {
                lock.lock();
                while(true) {
                    if(state && task != null) {
                        task.run();
                        task = null;
                        pool.offer(this);
                    } else {
                    	waitForTask.await();
                    }
                }
            }catch(InterruptedException i) {

            }finally {
                lock.unlock();
            }
        }

    }

    private Worker workerFactory(String name) {
        Worker worker = new Worker();
        worker.setWorkerName(name);
        worker.startWorker();
        return worker;
    }
}

