package com.rtexperiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import javax.realtime.AbsoluteTime;
import javax.realtime.AsyncEventHandler;
import javax.realtime.Clock;
import javax.realtime.OneShotTimer;
import javax.realtime.PeriodicParameters;
import javax.realtime.PeriodicTimer;
import javax.realtime.PriorityParameters;
import javax.realtime.PriorityScheduler;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.sound.midi.Soundbank;

public class TimerAndMonitor {

	public static void main(String[] args) {
		final int numberOfThreads 	 = 5;
		final int numberOfLoops   	 = 1;
		RealtimeThread[] myThreads   = new RealtimeThread[numberOfThreads];
	    AbsoluteTime[][] threadTimes = new AbsoluteTime  [numberOfThreads]
	    											     [numberOfLoops];


	    int priority = PriorityScheduler.instance().getMaxPriority() - 1;
	    PriorityParameters priorityParameters = new PriorityParameters(priority);
	    /* period: 200ms */
	    RelativeTime period = new RelativeTime(0 /* ms */, 100 /* ns */);
	    /* release parameters for periodic thread: */
	    PeriodicParameters periodicParameters = new PeriodicParameters(null,period, null,null,null,null);

	    
	    Calendar cal = Calendar.getInstance();
	    // TODO: make start time dependent on number of threads
	    cal.add(Calendar.SECOND, 2);
	    
	    AbsoluteTime startTime = new AbsoluteTime(cal.getTime());
	    
	    MyMonitor myMonitor = new MyMonitor();
		AsyncEventHandler myHandler = new AsyncEventHandler(myMonitor);
		PeriodicTimer myTimer = new PeriodicTimer(startTime, new RelativeTime(1,0), myHandler);
		myTimer.start();
		

	    for (int i = 0; i < numberOfThreads; i++) {
	    	final int k = i;
	    	
	    	
		    RealtimeThread realtimeThread = new RealtimeThread(priorityParameters,periodicParameters)
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

		    /* start periodic thread: */
		    realtimeThread.start();
		    
		    myThreads[i] = realtimeThread;
		}
	    
		for (int i = 0; i < numberOfThreads; i++) {
			try {
				myThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		// calculating the minimum and maximum finishing times of the threads
		AbsoluteTime[] minimumTime = new AbsoluteTime[numberOfLoops];
		AbsoluteTime[] maximumTime = new AbsoluteTime[numberOfLoops];
		
		
		for (int i = 0; i < numberOfThreads; i++) {
			for (int j = 0; j < numberOfLoops; j++) {
				if (minimumTime[j] == null || threadTimes[i][j].compareTo(minimumTime[j]) < 0 )
					minimumTime[j] = threadTimes[i][j];
				
				if (maximumTime[j] == null || threadTimes[i][j].compareTo(maximumTime[j]) > 0 )
					maximumTime[j] = threadTimes[i][j];
				
			}
		}
		
		
		// calculating the maximum delay overall
		RelativeTime maximumDelay = new RelativeTime();
		
		for (int i = 0; i < numberOfLoops; i++) {
			// TODO: subtract absolute times
			RelativeTime temp = maximumTime[i].subtract(minimumTime[i]);
			
			if (temp.compareTo(maximumDelay) > 0)
				maximumDelay = temp;
		}
		
		System.out.println("Maximum delay: " + maximumDelay);
		

		
	    
	    

		// TODO: go through each line and calculate global minimum and maximum		
	    // TODO: determine largest latency between the fastest and the slowest thread
	    // TODO: Create class for common functionalities 
		// TODO: delete expensive operations during critical section of realtime thread
	}
}