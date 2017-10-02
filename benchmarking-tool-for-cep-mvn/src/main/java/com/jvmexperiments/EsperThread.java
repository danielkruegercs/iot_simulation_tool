package com.jvmexperiments;

import java.util.Date;

import javax.realtime.Clock;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.rtexperiments.IntEvent;

public class EsperThread extends Thread {
	
	private int numberOfLoops;
	private int myThreadNumber;
	private MyMonitor myMonitor;
	private long[] threadTimes;
	private Date myDate;
	private EPServiceProvider senderEngine;
	private EPRuntime myRuntime;
	

	public long[] getThreadTimes() {
		return threadTimes;
	}

	public EsperThread(int numberOfLoops, MyMonitor myMonitor, int myThreadNumber) {
		this.senderEngine 	= EPServiceProviderManager.getDefaultProvider();
    	this.myRuntime 		= this.senderEngine.getEPRuntime();
		this.numberOfLoops 	= numberOfLoops;
		this.myMonitor     	= myMonitor;
		this.threadTimes   	= new long[numberOfLoops];
		this.myDate        	= new Date();
		this.myThreadNumber = myThreadNumber;
	}

	@Override
	public void run() {
		for (int j = 0; j < numberOfLoops; j++) {
			synchronized (myMonitor) {
				try {
					myMonitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("Sending event");
			EPServiceProvider otherEngine = EPServiceProviderManager.getDefaultProvider();
			otherEngine.getEPRuntime().sendEvent(new IntEvent(1,1,1));
        }
	}
}
