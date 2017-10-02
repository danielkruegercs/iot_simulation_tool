package com.jvmexperiments;

import java.util.Date;

import javax.realtime.Clock;

public class MyThread extends Thread {
	
	private int numberOfLoops;
	private MyMonitor myMonitor;
	private long[] threadTimes;
	private Date myDate;

	public long[] getThreadTimes() {
		return threadTimes;
	}

	public MyThread(int numberOfLoops, MyMonitor myMonitor) {
		this.numberOfLoops = numberOfLoops;
		this.myMonitor     = myMonitor;
		this.threadTimes   = new long[numberOfLoops];
		this.myDate        = new Date();
	}

	@Override
	public void run() {
		for (int j = 0; j < numberOfLoops; j++) {
			synchronized (myMonitor) {
				try {
					myMonitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			threadTimes[j] = myDate.getTime();
        }
	}
}
