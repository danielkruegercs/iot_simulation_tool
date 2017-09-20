


import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.realtime.PriorityScheduler;
import javax.realtime.PriorityParameters;
import javax.realtime.PeriodicParameters;
import javax.realtime.RelativeTime;
import javax.realtime.RealtimeThread;



public class ReceiverSenderThread extends Thread {

    private boolean stopRequested = false;
    private int port = -1;
    private BenchmarkManager myManager;
    private int numberOfLoops;


    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }


    public ReceiverSenderThread(int pPortNumber, BenchmarkManager myManager, int pNumberOfLoops) {
        port = pPortNumber;
        this.myManager = myManager;
        numberOfLoops = pNumberOfLoops;
    }

    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(port);



            // run until stopped by BenchmarkThreadManager
            while(!stopRequested) {
//            for (int i = 0; i < numberOfLoops / 4; i++) {
			
						
                Socket socket = listener.accept();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    EPServiceProvider receiverSenderEngine = EPServiceProviderManager.getDefaultProvider();
                    // TODO: support boolean and float events later

                    String tcpInput = in.readLine();
                    
                    if (tcpInput.equals("benchmarkFinished")) {
                    	myManager.setBenchmarkFinished(true);
                    	break;
                    }
                    	
                    receiverSenderEngine.getEPRuntime().sendEvent(new IntEvent(Integer.valueOf(tcpInput)));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                socket.close();
            }
            
            myManager.setBenchmarkFinished(true);

            listener.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
