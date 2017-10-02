package com.jvmexperiments;

import java.util.TimerTask;

import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;

public class MyMonitor extends TimerTask {

	@Override
	public void run() {
		synchronized(this){
			notifyAll();
        }
	}
}
