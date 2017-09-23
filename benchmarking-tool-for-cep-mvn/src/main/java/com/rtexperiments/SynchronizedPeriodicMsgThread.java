package com.rtexperiments;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RealtimeThread;

public class SynchronizedPeriodicMsgThread extends RealtimeThread {

	public SynchronizedPeriodicMsgThread(MyMonitor myMonitor, PriorityParameters priorityParameters, PeriodicParameters periodicParameters) {
		super(priorityParameters,periodicParameters);

	}
}
