import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.PriorityScheduler;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;


/**
 * Created by daniel on 21.06.17.
 */
public class BenchmarkThreadManager extends Thread {
    // beginning of attributes
    private ArrayList<ReceiverSenderThread> myThreads = new ArrayList<ReceiverSenderThread>();

    private int numberOfThreads = -1;
    // end of attributes


    // beginning of getters and setters

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    // end of getters and setters


    public BenchmarkThreadManager(int pNumberOfThreads) {
        numberOfThreads = pNumberOfThreads;
    }

    
    public void startThreads(BenchmarkManager myManager, int pNumberOfLoops) {
        for (int i = 0; i < numberOfThreads; i++) {
            int newPortNumber = 1234 + i;
            
			int priority = PriorityScheduler.instance().getMinPriority()+10;
			PriorityParameters priorityParameters = new PriorityParameters(priority);
			
			    /* period: 200ms */
			RelativeTime period = new RelativeTime(0 /* ms */, 1000 /* ns */);
			
			/* release parameters for periodic thread: */
			PeriodicParameters periodicParameters = new PeriodicParameters(null,period, null,null,null,null);
			
			/* create periodic thread: */
			RealtimeThread realtimeThread = new RealtimeThread(priorityParameters,periodicParameters)
			{
			  public void run()
			  {
				  try {
			        ServerSocket listener = new ServerSocket(newPortNumber);
			        System.out.println("Listening on port: " + newPortNumber);
			
			        while(true) {
			            Socket socket = listener.accept();
			            try {
			                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			                EPServiceProvider receiverSenderEngine = EPServiceProviderManager.getDefaultProvider();
			
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
			    }
			    catch (IOException e) {
			        e.printStackTrace();
			    }
			  }
			};
			
			/* start periodic thread: */
			realtimeThread.start();       
        }
        
    }
}