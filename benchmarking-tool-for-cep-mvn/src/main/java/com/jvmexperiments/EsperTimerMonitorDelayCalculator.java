package com.jvmexperiments;

import java.util.Date;
import java.util.Timer;

import javax.realtime.AbsoluteTime;
import javax.realtime.AsyncEventHandler;
import javax.realtime.Clock;
import javax.realtime.PeriodicTimer;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;

public class EsperTimerMonitorDelayCalculator {

	public static void main(String[] args) {
		int numberOfThreads			   = 1;
		int numberOfLoops   		   = 1;
		int intervalPeriod  		   = 1;
		double startTimeRoundParameter = 0.01;
		int startDelay 				   = Math.max(1000, (int) Math.round(numberOfThreads * startTimeRoundParameter));
		EsperThread[] myThreads		   = new EsperThread[numberOfThreads];
		long[][] threadTimes 		   = new long[numberOfThreads]
	    										[numberOfLoops];
		MyMonitor myMonitor 	       = new MyMonitor();
		Timer myTimer 				   = new Timer();
		myTimer.schedule(myMonitor, startDelay, intervalPeriod);
		
		BenchmarkQuery myQuery = new BenchmarkQuery(threadTimes, numberOfLoops);
		myQuery.query();
		
		
		// running threads
	    for (int i = 0; i < numberOfThreads; i++) {
	    	final int k = i;
	    	
	    	
		    EsperThread someThread = new EsperThread(numberOfLoops, myMonitor, k);

		    someThread.start();
		    myThreads[i] = someThread;
		}
	    
	    // waiting for all threads to end
		for (int i = 0; i < numberOfThreads; i++) {
			try {
				myThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		threadTimes = myQuery.getThreadTimes();
		
		// calculating minimum and maximum delay loop-wise
		long[] minimumTime = new long[numberOfLoops];
		long[] maximumTime = new long[numberOfLoops];
		
		
		for (int i = 0; i < numberOfThreads; i++) {
			for (int j = 0; j < numberOfLoops; j++) {
				if (minimumTime[j] == 0 || threadTimes[i][j] - minimumTime[j] < 0 )
					minimumTime[j] = threadTimes[i][j];
				
				if (maximumTime[j] == 0 || threadTimes[i][j] - maximumTime[j] > 0 )
					maximumTime[j] = threadTimes[i][j];
				
			}
		}
		
		
		// calculating the maximum delay overall
		long maximumDelay = 0;
		System.out.println(maximumDelay);
		
		for (int i = 0; i < numberOfLoops; i++) {
			long temp = maximumTime[i] - minimumTime[i];
			
			
			if (temp - maximumDelay > 0)
				maximumDelay = temp;
		}
		

		
		
		// TODO: create report file
		System.out.println("Maximum delay: " + maximumDelay);
		myTimer.cancel();
	}
}