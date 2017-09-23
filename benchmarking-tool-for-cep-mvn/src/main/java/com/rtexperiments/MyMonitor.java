package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;

public class MyMonitor implements Runnable {

	@Override
	public void run() {
		synchronized(this){
			notifyAll();
        }
	}
	
	public void waitTillNextFire() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
