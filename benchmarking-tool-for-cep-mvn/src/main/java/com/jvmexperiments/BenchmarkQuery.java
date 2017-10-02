package com.jvmexperiments;



import java.util.Date;

import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;
import javax.sound.midi.Soundbank;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class BenchmarkQuery {
	private long[][] threadTimes;
	private EPServiceProvider queryEngine = EPServiceProviderManager.getDefaultProvider();
	private int numberOfLoops;
	
	
	public EPServiceProvider getQueryEngine() {
		return this.queryEngine;
	}

	public long[][] getThreadTimes() {
		return this.threadTimes;
	}

	public BenchmarkQuery(long[][] threadTimes, int numberOfLoops) {
		this.threadTimes = threadTimes;
		this.numberOfLoops = numberOfLoops;
	}
	
    
	public void query() {
		EPServiceProvider queryEngine = EPServiceProviderManager.getDefaultProvider();
		queryEngine.getEPAdministrator().getConfiguration().addEventType(IntEvent.class);
		String epl = "select * from IntEvent";
		EPStatement statement = queryEngine.getEPAdministrator().createEPL(epl);
		statement.addListener( (newData, oldData) -> {
		  int myValue = (int) newData[0].get("myValue");
		  
		  System.out.println(String.format("MyValue: %d", myValue));
		});
	}
}