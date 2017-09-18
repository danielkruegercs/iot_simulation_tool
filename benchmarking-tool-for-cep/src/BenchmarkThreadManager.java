





import java.util.ArrayList;


/**
 * Created by daniel on 21.06.17.
 */
public class BenchmarkThreadManager extends Thread {
    // beginning of attributes
    private ArrayList<ReceiverSenderThread> myThreads = new ArrayList<>();

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
            ReceiverSenderThread newThread = new ReceiverSenderThread(newPortNumber, myManager, pNumberOfLoops);
            this.myThreads.add(newThread);
            newThread.start();
        }
    }

    public void stopReceiverSenderThreads() {
        // request all threads to stop
        for (ReceiverSenderThread iterThread : this.myThreads)
            iterThread.setStopRequested(true);

        // delete old reference by setting pointer to null
        this.myThreads = null;
        this.myThreads = new ArrayList<>();
    }
}