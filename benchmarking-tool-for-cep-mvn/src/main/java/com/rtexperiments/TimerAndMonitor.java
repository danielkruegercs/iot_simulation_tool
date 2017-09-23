package com.rtexperiments;

import java.io.File;
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
		final int numberOfThreads 	 = 1;
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					threadTimes[k][j] = new AbsoluteTime(Clock.getRealtimeClock().getTime());
					System.out.println("threadTimes " + k + j);
					System.out.println(threadTimes[k][j]);
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
		
		// writing threadtimes into files
		for (int i = 0; i < numberOfThreads; i++)
			for (int j = 0; j < numberOfLoops; j++) {
				FileWriter fw;
				try {
					// TODO: create dir 'threadTimes' dynamically
					File myFile = new File("./threadTimes/thread" + i + ".txt");
					myFile.createNewFile();
					fw = new FileWriter(myFile);
					
					fw.write(threadTimes[i][j].toString());
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    
	    
	    // TODO: read file contents and compare seconds, milliseconds
		// TODO: go through each line and calculate global minimum and maximum		
	    // TODO: determine largest latency between the fastest and the slowest thread
	    // TODO: Create class for common functionalities 
		// TODO: delete expensive operations during critical section
	}
}