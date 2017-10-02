package com.jvmexperiments;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class Espertest {
	
	public static void query() {
		EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();
		engine.getEPAdministrator().getConfiguration().addEventType(IntEvent.class);
		String epl = "select * from IntEvent";
		EPStatement statement = engine.getEPAdministrator().createEPL(epl);
		statement.addListener( (newData, oldData) -> {
		  int myValue = (int) newData[0].get("myValue");
		  
		  System.out.println(String.format("MyValue: %d", myValue));
		});
	}
	
	public static void sendStuff() {
		EPServiceProvider otherEngine = EPServiceProviderManager.getDefaultProvider();
		otherEngine.getEPRuntime().sendEvent(new IntEvent(1,1,1));
	}

	public static void main(String[] args) {
		query();
		sendStuff();
	}
}
