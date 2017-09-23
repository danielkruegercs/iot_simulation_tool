package com.rtexperiments;
import java.util.Calendar;
import java.util.Date;

import javax.realtime.*;

public class TestingRT {
	
	public static void main(String[] args) {
		int priority = PriorityScheduler.instance().getMaxPriority() - 1;
	    PriorityParameters priorityParameters = new PriorityParameters(priority);

	    /* period: 200ms */
	    RelativeTime period = new RelativeTime(0 /* ms */, 1000 /* ns */);

	    /* release parameters for periodic thread: */
	    PeriodicParameters periodicParameters = new PeriodicParameters(null,period, null,null,null,null);

	    
	    int numberOfThreads = 5;
	    long[] threadTimes = new long[numberOfThreads];
	    
	    
	    Calendar cal = Calendar.getInstance();
	    System.out.println("current date: " + cal.getTime());
	    cal.add(Calendar.SECOND, 1);
	    System.out.println("1 second later: " + cal.getTime());
	    
	    AbsoluteTime startTime = new AbsoluteTime(cal.getTime());
	    System.out.println(startTime);
	    
	    
	    for (int i = 0; i < numberOfThreads; i++) {
	    	/* create periodic thread: */
		    RealtimeThread realtimeThread = new RealtimeThread(priorityParameters,periodicParameters)
		    {
		      public void run()
		      {
		    	AbsoluteTime myTime;
		    	
		    	while(true)
//		    	for (int j = 0; j < 2; j++)
		        {
					try {
						waitForNextRelease();
					} catch (IllegalThreadStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					if (Clock.getRealtimeClock().getTime().compareTo(startTime) >= 0) {
//						myTime = System.currentTimeMillis();
						myTime = Clock.getRealtimeClock().getTime();
						break;
					}
		        }
		    	
		    	System.out.println(myTime);
		      }
		    };

		    /* start periodic thread: */
		    realtimeThread.start();	
		}
	}
}
