package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.AsyncEventHandler;
import javax.realtime.Clock;
import javax.realtime.PeriodicTimer;
import javax.realtime.RelativeTime;

public class AsyncEventTimeFetcher implements Runnable {

	private int j = 0;
	private AbsoluteTime[] threadTimes;
	private PeriodicTimer myTimer;
		
	
	public AbsoluteTime[] getThreadTimes() {
		return threadTimes;
	}


	public AsyncEventTimeFetcher(AbsoluteTime[] threadTimes, AbsoluteTime startTime, RelativeTime interval) {
		AsyncEventHandler myHandler = new AsyncEventHandler(this);
		this.threadTimes = threadTimes;
		myTimer = new PeriodicTimer(startTime, interval, myHandler);
		myTimer.start();
	}

	@Override
	public void run() {
		if (j == this.threadTimes.length) {
			myTimer.stop();
			synchronized (this) {
				notify();	
			}
		}
		else {
			this.threadTimes[j] = Clock.getRealtimeClock().getTime();
			j++;	
		}
	}
}
