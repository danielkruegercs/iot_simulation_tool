package de.unistuttgart.ipvs;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import de.unistuttgart.ipvs.IntEvent;

public class SendEvents {

    public static void sendEvents() {

        EPServiceProvider otherEngine = EPServiceProviderManager.getDefaultProvider();


        otherEngine.getEPRuntime().sendEvent(new IntEvent(10));

    }
}
