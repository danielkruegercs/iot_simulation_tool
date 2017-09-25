package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;

public class TimerThreadsDelayCalculator {
	
	public static void main(String[] args) {
		
		BenchmarkAttributeObject myAttributes = new BenchmarkAttributeObject();
		myAttributes.initializeAttributes();
	    int numberOfLoops = myAttributes.getNumberOfLoops();
	    AbsoluteTime[][] threadTimes = new AbsoluteTime [myAttributes.getNumberOfThreads()]
	    												[numberOfLoops];
	    AsyncEventTimeFetcher[] myFetchers = new AsyncEventTimeFetcher[myAttributes.getNumberOfThreads()];


	    for (int i = 0; i < myAttributes.getNumberOfThreads(); i++) {
	    	final int k = i;
    	
	    	
		    RealtimeThread realtimeThread = new RealtimeThread(myAttributes.getPriorityParameters(),myAttributes.getPeriodicParameters())
		    {
		      public void run()
		      {
		    	myFetchers[k] = new AsyncEventTimeFetcher(threadTimes[k], myAttributes.getStartTime(), new RelativeTime(myAttributes.getIntervalPeriod(),0));
		    	
		    	try {
		    		synchronized (myFetchers[k]) {
		    			myFetchers[k].wait();	
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		      }
		    };

		    realtimeThread.start();
		    myAttributes.getMyThreads()[i] = realtimeThread;
		}
	    
		for (int i = 0; i < myAttributes.getNumberOfThreads(); i++) {
			try {
				if (myAttributes.getMyThreads()[i] != null)
					myAttributes.getMyThreads()[i].join();
				threadTimes[i] = myFetchers[i].getThreadTimes();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		RelativeTime maximumDelay = DelayCalculator.calculateMaximumDelayBetweenThreads(threadTimes, myAttributes.getNumberOfThreads(), numberOfLoops);
		
		System.out.println("Maximum delay: " + maximumDelay);
	}

}
