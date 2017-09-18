package SimulationToolForTheInternetOfThings;

import java.util.ArrayList;


/**
 * Created by daniel on 21.06.17.
 */
public class ThreadManager extends Thread {
    private int frequency = -1;
    private SensorThreadClock threadClock = null;


    public ThreadManager (int frequency) {
        this.frequency = frequency;
    }



    // beginning of attributes
    private ArrayList<SensorSimulatorThread> myThreads = new ArrayList<>();

    private int numberOfThreads = -1;
    private String broker       = "";
    // end of attributes


    // beginning of getters and setters
    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    // end of getters and setters


    public void changeThreads(String inputDatatype,
                              String startingValue,
                              String maxNegSpike,
                              String maxPosSpike,
                              String dY,
                              float anomalyProbability,
                              int frequency) {

        for (SensorSimulatorThread iterThread : this.myThreads)
            iterThread.setStopRequested(true);


        // delete old reference by setting pointer to null
        this.myThreads = null;
        this.myThreads = new ArrayList<>();

        for (int i = 0; i < this.numberOfThreads; i++)
            startNewThread(inputDatatype,
                    startingValue,
                    maxNegSpike,
                    maxPosSpike,
                    dY,
                    anomalyProbability,
                    frequency,
                    i,
                    threadClock);
    }



    public void startNewThread( String inputDatatype,
                                String startingValue,
                                String maxNegSpike,
                                String maxPosSpike,
                                String dY,
                                float anomalyProbability,
                                int frequency,
                                int threadId,
                                SensorThreadClock threadClock) {

        long startingTime = -1;

        if (this.numberOfThreads != -1)
            startingTime = System.nanoTime() + 10000;

        SensorSimulatorThread myThread = new SensorSimulatorThread( inputDatatype,
                                                                    startingValue,
                                                                    maxNegSpike,
                                                                    maxPosSpike,
                                                                    dY,
                                                                    anomalyProbability,
                                                                    frequency,
                                                                    threadId,
                                                                    broker,
                                                                    startingTime,
                                                                    threadClock);
        myThread.setPriority(Thread.MAX_PRIORITY);
        myThread.start();
        this.myThreads.add(myThread);
    }

    public void stopAllThreads() {
        // request all threads to stop
        for (SensorSimulatorThread iterThread : this.myThreads)
            iterThread.setStopRequested(true);


        // delete old reference by setting pointer to null
        this.myThreads = null;
        this.myThreads = new ArrayList<>();
    }

    @Override
    public synchronized void run() {

        while (true) {
            System.out.println("Notifying threads");

            for (SensorSimulatorThread iterThread : myThreads)
                iterThread.notify();

            try {
                Thread.sleep(Long.valueOf(frequency));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
