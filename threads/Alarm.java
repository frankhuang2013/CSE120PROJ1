package nachos.threads;

import nachos.machine.*;
import java.util.HashMap;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	
	private HashMap<KThread,Long> waitThread = new HashMap<>();
	
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		Machine.interrupt().disable();
		KThread t = KThread.currentThread();
		int inTheList = 0;
		//System.out.println("size: " + a + this.waitThread.isEmpty());
		for (HashMap.Entry<KThread,Long> entry : waitThread.entrySet()) {
			System.out.println("long: " + entry.getValue());
			if (Machine.timer().getTime() > waitThread.get(entry.getValue())){
				waitThread.remove(entry.getKey());
			}
			else if (t == entry.getKey()){
				inTheList = 1;	
			}
		}
		if (inTheList == 0){
			KThread.currentThread().yield();
		}
		//Machine.interrupt().enable();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
		//KThread.yield();
		Machine.interrupt().disable();
		KThread.yield();
		long wakeTime = Machine.timer().getTime() + x;
		this.waitThread.put(KThread.currentThread(), wakeTime);
		//System.out.println("value: " + a + " " + waitThread.get(KThread.currentThread()));
		//System.out.println("bool: " + waitThread.size());
		//Machine.interrupt().enable();	
		//while (wakeTime > Machine.timer().getTime())
		//	KThread.yield();
	}



	

	public static void alarmTest1() {
		int durations[] = {1000, 10*1000, 100*1000};
		long t0, t1;

		for (int d : durations) {
	    		t0 = Machine.timer().getTime();
	    		ThreadedKernel.alarm.waitUntil (d);
	   		 t1 = Machine.timer().getTime();
	    		System.out.println ("alarmTest1: waited for " + (t1 - t0) + " ticks");
		}
    	}

    	public static void selfTest() {
    		alarmTest1(); 
    	}
}
