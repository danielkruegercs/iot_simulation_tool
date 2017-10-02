package com.rtexperiments;



import javax.realtime.AbsoluteTime;
import javax.realtime.Clock;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class BenchmarkQuery extends Thread {
	private AbsoluteTime[][] threadTimes;
	private EPServiceProvider queryEngine = EPServiceProviderManager.getDefaultProvider();
	
	public EPServiceProvider getQueryEngine() {
		return this.queryEngine;
	}

	public AbsoluteTime[][] getThreadTimes() {
		return this.threadTimes;
	}

	public BenchmarkQuery(AbsoluteTime[][] threadTimes) {
		this.threadTimes = threadTimes;
	}
	
    @Override
	public void run() {
		// TODO Auto-generated method stub
		this.queryEngine.getEPAdministrator().getConfiguration().addEventType(IntEvent.class);
        
        String epl = "select myValue from IntEvent";
        EPStatement statement = this.queryEngine.getEPAdministrator().createEPL(epl);
        
        
        
        statement.addListener( (newData, oldData) -> {
        	int threadNumber = (int) newData[0].get("threadNumber");
        	int loopNumber = (int) newData[0].get("loopNumber");
            int myValue = (int) newData[0].get("myValue");
            System.out.println("got a new value");
            this.threadTimes[threadNumber][loopNumber] = Clock.getRealtimeClock().getTime();
//            System.out.println(String.format("New Random value: %d, Received time: %d", myValue, System.currentTimeMillis()));
        });
        System.out.println("Query started");
		
	}
}