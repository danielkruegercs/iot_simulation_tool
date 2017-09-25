package com.rtexperiments;

import java.util.Calendar;

import javax.realtime.AbsoluteTime;
import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.PriorityScheduler;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;

public class BenchmarkAttributeObject {
	
	// primitive type variables
	// numberOfThreads and numberOfLoops should only be set in one place
	// TODO: put values into config file and read values from file
	private final int numberOfThreads 	 		  		= 500;
	private final int numberOfLoops   	 		  		= 1;
	private final int threadPeriod				  		= 1000; /* ns */
	private final int intervalPeriod					= 1;    /* ms */
	private final double startTimeRoundParameter  		= 0.01;
	private final int numberOfSeconds 			  		= (int) Math.round(numberOfThreads * startTimeRoundParameter);
	
	
	// arrays
	private AbsoluteTime[][] threadTimes 		  		= new AbsoluteTime   [numberOfThreads]
											     							 [numberOfLoops];
	private RealtimeThread[] myThreads 					= new RealtimeThread [numberOfThreads];

	
	// realtime thread parameters
	private final int priority 				    		= PriorityScheduler.instance().getMaxPriority() - 1;
    private final PriorityParameters priorityParameters = new PriorityParameters(priority);
    private final RelativeTime period 				    = new RelativeTime(0 /* ms */, threadPeriod /* ns */);
    private final PeriodicParameters periodicParameters = new PeriodicParameters(null,period, null,null,null,null);

    
	// time parameters
    private Calendar cal 						  		= Calendar.getInstance();
    private AbsoluteTime startTime;

    


	public int getIntervalPeriod() {
		return intervalPeriod;
	}
    
	public double getStartTimeRoundParameter() {
		return startTimeRoundParameter;
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public int getNumberOfLoops() {
		return numberOfLoops;
	}

	public int getThreadPeriod() {
		return threadPeriod;
	}

	public int getNumberOfSeconds() {
		return numberOfSeconds;
	}

	public AbsoluteTime[][] getThreadTimes() {
		return threadTimes;
	}

	public RealtimeThread[] getMyThreads() {
		return myThreads;
	}

	public int getPriority() {
		return priority;
	}

	public PriorityParameters getPriorityParameters() {
		return priorityParameters;
	}

	public RelativeTime getPeriod() {
		return period;
	}

	public PeriodicParameters getPeriodicParameters() {
		return periodicParameters;
	}

	public Calendar getCal() {
		return cal;
	}

	public AbsoluteTime getStartTime() {
		return startTime;
	}


    public void setThreadTimes(AbsoluteTime[][] threadTimes) {
		this.threadTimes = threadTimes;
	}

	public void setMyThreads(RealtimeThread[] myThreads) {
		this.myThreads = myThreads;
	}
	
	
	public void initializeAttributes() {
		cal.add(Calendar.SECOND, numberOfSeconds);
		startTime = new AbsoluteTime(cal.getTime());
	}

}
