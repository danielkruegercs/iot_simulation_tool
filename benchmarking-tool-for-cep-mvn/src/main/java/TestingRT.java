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

	    
	    int numberOfThreads = 10;
	    long startTime = System.currentTimeMillis() + (numberOfThreads * 100);
	    long[] threadTimes = new long[numberOfThreads];
	    
	    System.out.println("Start time");
	    System.out.println(startTime);
	    
	    System.out.println("Trying out more threads");
	    
	    for (int i = 0; i < numberOfThreads; i++) {
	    	/* create periodic thread: */
		    RealtimeThread realtimeThread = new RealtimeThread(priorityParameters,periodicParameters)
		    {
		      public void run()
		      {
		    	AbsoluteTime myTime;
		    	
		    	
		    	while(true)
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
					if (System.currentTimeMillis() >= startTime) {
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
