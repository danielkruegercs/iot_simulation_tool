package com.rtexperiments;

import javax.realtime.AbsoluteTime;
import javax.realtime.AsyncEventHandler;
import javax.realtime.Clock;
import javax.realtime.PeriodicTimer;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.Timer;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class EsperMonitorDelayCalculator {

	public static void main(String[] args) {
		BenchmarkAttributeObject myAttributes = new BenchmarkAttributeObject();
		myAttributes.initializeAttributes();
	    int numberOfLoops 					  = myAttributes.getNumberOfLoops();
		MyRTMonitor myMonitor 				  = new MyRTMonitor();
		AsyncEventHandler myHandler 		  = new AsyncEventHandler(myMonitor);
		PeriodicTimer myTimer 				  = new PeriodicTimer(myAttributes.getStartTime(), 
														          new RelativeTime(myAttributes.getIntervalPeriod(), 0), 
														          myHandler);
		BenchmarkQuery myBenchmarkQuery = new BenchmarkQuery(new AbsoluteTime[myAttributes.getNumberOfThreads()][numberOfLoops]);
		myBenchmarkQuery.start();
		
		
		
		myTimer.start();
		

		// running threads
	    for (int i = 0; i < myAttributes.getNumberOfThreads(); i++) {
	    	final int k = i;
	    	
	    	
		    RealtimeThread realtimeThread = new RealtimeThread(myAttributes.getPriorityParameters(),
		    												   myAttributes.getPeriodicParameters())
		    {

		      public void run()
		      {
		    	EPServiceProvider senderEngine = myBenchmarkQuery.getQueryEngine();
		    	EPRuntime myRuntime = senderEngine.getEPRuntime();
		    	
		    	System.out.println("created engine");
		    	
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
					
		    		
		    		myRuntime.sendEvent(new IntEvent(k, j, Integer.valueOf(1)));
		    		System.out.println("send event");
				}
		      }
		      

		    };
		    
		    myAttributes.getMyThreads()[i] = realtimeThread;
		    realtimeThread.start();
		}
	    

	    // waiting for all threads to end
		for (int i = 0; i < myAttributes.getNumberOfThreads(); i++) {
			try {
				myAttributes.getMyThreads()[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// calculating maximum delay
		System.out.println(myBenchmarkQuery);
		System.out.println(myBenchmarkQuery.getThreadTimes()[0]);
		RelativeTime maximumDelay = DelayCalculator.calculateMaximumDelayBetweenThreads(myBenchmarkQuery.getThreadTimes(), 
																						myAttributes.getNumberOfThreads(), 
																						numberOfLoops);
		// TODO: create report file
		System.out.println("Maximum delay: " + maximumDelay);    
	}
}