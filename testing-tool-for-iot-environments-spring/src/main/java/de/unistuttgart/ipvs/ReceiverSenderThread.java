package de.unistuttgart.ipvs;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import de.unistuttgart.ipvs.IntEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverSenderThread extends Thread {

    private boolean stopRequested = false;
    private int port = -1;


    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }


    public ReceiverSenderThread(int pPortNumber) {
        port = pPortNumber;
    }

    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(port);



            // run until stopped by BenchmarkThreadManager
            while(!stopRequested) {
                Socket socket = listener.accept();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    EPServiceProvider receiverSenderEngine = EPServiceProviderManager.getDefaultProvider();
                    // TODO: support boolean and float events later

                    receiverSenderEngine.getEPRuntime().sendEvent(new IntEvent(Integer.valueOf(in.readLine())));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                socket.close();
            }

            listener.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
