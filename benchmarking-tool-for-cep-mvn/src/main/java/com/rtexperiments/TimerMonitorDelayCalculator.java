package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.AsyncEventHandler;
import javax.realtime.Clock;
import javax.realtime.PeriodicTimer;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;

public class TimerMonitorDelayCalculator {

	public static void main(String[] args) {
		BenchmarkAttributeObject myAttributes = new BenchmarkAttributeObject();
		myAttributes.initializeAttributes();
	    int numberOfLoops 					  = myAttributes.getNumberOfLoops();
		AbsoluteTime[][] threadTimes 		  = new AbsoluteTime [myAttributes.getNumberOfThreads()]
	    														 [numberOfLoops];
		MyMonitor myMonitor 				  = new MyMonitor();
		AsyncEventHandler myHandler 		  = new AsyncEventHandler(myMonitor);
		PeriodicTimer myTimer 				  = new PeriodicTimer(myAttributes.getStartTime(), 
														          new RelativeTime(myAttributes.getIntervalPeriod(), 0), 
														          myHandler);
		myTimer.start();
		

		// running threads
	    for (int i = 0; i < myAttributes.getNumberOfThreads(); i++) {
	    	final int k = i;
	    	
	    	
		    RealtimeThread realtimeThread = new RealtimeThread(myAttributes.getPriorityParameters(),
		    												   myAttributes.getPeriodicParameters())
		    {

		      public void run()
		      {
		    	AbsoluteTime myTime;
		    	
		    	for (int j = 0; j < numberOfLoops; j++) {
		    		try {
						waitForNextRelease();
						synchronized (myMonitor) {
				    			myMonitor.wait();
						}
					} catch (IllegalThreadStateException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					
					threadTimes[k][j] = Clock.getRealtimeClock().getTime();
					
		        }
		      }
		      

		    };

		    realtimeThread.start();
		    myAttributes.getMyThreads()[i] = realtimeThread;
		}
	    
	    // waiting for all threads to end
		for (int i = 0; i < myAttributes.getNumberOfThreads(); i++) {
			try {
				System.out.println(myAttributes.getMyThreads()[i]);
				myAttributes.getMyThreads()[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// calculating maximum delay
		RelativeTime maximumDelay = DelayCalculator.calculateMaximumDelayBetweenThreads(threadTimes, 
																						myAttributes.getNumberOfThreads(), 
																						numberOfLoops);
		// TODO: create report file
		System.out.println("Maximum delay: " + maximumDelay);
	}
}