package de.unistuttgart.ipvs;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class BenchmarkingCEP {

    public static void main(String[] args) {

        EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();
        engine.getEPAdministrator().getConfiguration().addEventType(IntEvent.class);
        String epl = "select myValue from IntEvent";
        EPStatement statement = engine.getEPAdministrator().createEPL(epl);
        statement.addListener( (newData, oldData) -> {
            int myValue = (int) newData[0].get("myValue");
            System.out.println(String.format("MyValue: %d", myValue));
        });
        SendEvents.sendEvents();

    }
}
