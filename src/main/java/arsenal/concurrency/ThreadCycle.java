package arsenal.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadCycle {

	private static Logger logger = LoggerFactory.getLogger(ThreadCycle.class);

	private final List<Task> tasks;
	
	//latch to hold the first task until made open by client
	private final CountDownLatch startCycle = new CountDownLatch(1);
	
	public ThreadCycle(int numOfTask) {
		
		tasks = new ArrayList<Task>(numOfTask);
		for (int i = 0; i < numOfTask; i++) {
			tasks.add(new Task());
		}
		//chain the tasks
		for (int i = 0; i < numOfTask  ; i++) {
			Task task = tasks.get(i);
			task.setSeed(i + 1);
			Task nextTask = (i == (numOfTask -1)) ? tasks.get(0) : tasks.get(i + 1);
			task.setnTask(nextTask);
		}
		
	}
	
	public void start(){
		//set barrier on first task
		Task firstTask = tasks.get(0);
		firstTask.setLatch(startCycle);
		
		int size = tasks.size();
		//give each task to a worker Thread
		for (int i = 0; i < size; i++) {
			new Thread(tasks.get(i)).start();
		}
		//set the start flag firstTask
		firstTask.setpTaskDone(true);
		//notify the first worker, pull the latch
		startCycle.countDown();
		
	}
	
	private static class Task implements Runnable {
		
		/**
		 * Thread executing this task will wait on this lock.
		 * @param waitingLock
		 */
		private Object lock = new Object();
		
		private CountDownLatch latch;
		
		//Work chaining. Notify processing next task
		private Task nTask;
		
		//Condition predicate
		private volatile boolean pTaskDone;

		private int seed; 
		
		/**
		 * Each worker will loop through its task 
		 */
		public void run() {
			while(true){
				try{
					if(latch != null) {
						latch.await();
					}
					synchronized (lock) {
						while(!ispTaskDone()) {
							lock.wait();
						}
						//reset previous task status
						setpTaskDone(false);
					}
					
					doWork();
					//notify next worker thread
					synchronized (nTask.getLock()) {
						nTask.setpTaskDone(true);
						nTask.getLock().notify();
					}
				} catch(Exception ie) {
					logger.error(ie.getMessage());
				}
			}
		}
		
		private void doWork() throws InterruptedException {

			//do work
			String tName = Thread.currentThread().getName();
			logger.info(tName + " : " + seed);
			Random random  = new Random();
			Thread.sleep(random.nextInt(seed * 2000));
		}

		public Object getLock() {
			return lock;
		}

		public void setnTask(Task nTask) {
			this.nTask = nTask;
		}

		public boolean ispTaskDone() {
			return pTaskDone;
		}

		public void setpTaskDone(boolean pTaskDone) {
			this.pTaskDone = pTaskDone;
		}

		public void setLatch(CountDownLatch l) {
			this.latch = l;
		}

		public void setSeed(int seed){
			this.seed = seed;
		}
	
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadCycle cycle = new ThreadCycle(4);
		
		cycle.start();
		
	}

}
