package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;

public class MyRTMonitor implements Runnable {

	@Override
	public void run() {
		synchronized(this){
			notifyAll();
        }
	}
}
