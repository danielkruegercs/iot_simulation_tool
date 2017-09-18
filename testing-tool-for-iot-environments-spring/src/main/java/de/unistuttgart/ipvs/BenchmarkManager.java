package de.unistuttgart.ipvs;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class BenchmarkManager {

    public static void main(String[] args) {
        BenchmarkThreadManager myThreadManager = new BenchmarkThreadManager(5);
        myThreadManager.startThreads();
        BenchmarkQuery.query();

        System.out.println("yoooo");
        while(true) {

        }

//        myThreadManager.stopReceiverSenderThreads();

    }
}
