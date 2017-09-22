package SimulationToolForTheInternetOfThings;

import java.util.ArrayList;
import org.json.*;

/**
 * Created by daniel on 21.06.17.
 */
public class ThreadManager extends Thread {
    private int frequency = -1;


    public ThreadManager (int frequency) {
        this.frequency = frequency;
    }



    // beginning of attributes
    private ArrayList<SensorSimulatorThread> myThreads = new ArrayList<>();
    private ArrayList<Sensor> mySensors 			   = new ArrayList<>();
    
    
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
    
    public ArrayList<Sensor> getSensors() {
    	return this.mySensors;
    }
    // end of getters and setters


    public void createSensor( String inputDatatype,
                                String startingValue,
                                String maxNegSpike,
                                String maxPosSpike,
                                String dY,
                                float anomalyProbability,
                                int frequency,
                                String sensorId) {
    	
    	SensorSimulatorThread myThread = new SensorSimulatorThread( inputDatatype,
                                                                    startingValue,
                                                                    maxNegSpike,
                                                                    maxPosSpike,
                                                                    dY,
                                                                    anomalyProbability,
                                                                    frequency,
                                                                    sensorId);
        Sensor mySensor = new Sensor(inputDatatype,
                					 startingValue,
                					 maxNegSpike,
                					 maxPosSpike,
                					 dY,
                					 anomalyProbability,
                					 frequency,
                					 sensorId);
        myThread.setPriority(Thread.MAX_PRIORITY);
        myThread.start();
        
        
        this.myThreads.add(myThread);
        this.mySensors.add(mySensor);
    }
    
    public void updateSensor (String inputDatatype,
            				  String startingValue,
            				  String maxNegSpike,
            				  String maxPosSpike,
            				  String dY,
            				  float anomalyProbability,
            				  int frequency,
            				  String oldSensorId,
            				  String newSensorId) {
    	deleteSensor(oldSensorId);
    	createSensor(inputDatatype,
				  	 startingValue,
				  	 maxNegSpike,
				  	 maxPosSpike,
				  	 dY,
				  	 anomalyProbability,
				  	 frequency,
				  	 newSensorId);
    }
    
    public void deleteSensor (String sensorId) {
    	// removing thread
    	SensorSimulatorThread tempThread = null;
    	
    	for (SensorSimulatorThread iterThread : this.myThreads)
    		if (iterThread.getThreadId().equals(sensorId))
    			tempThread = iterThread;
    	
    	tempThread.setStopRequested(true);
    	this.myThreads.remove(tempThread);
    	tempThread = null;
	    
    	// removing sensor
    	Sensor tempSensor = null;
    	for (Sensor iterSensor : this.mySensors)
    		if (iterSensor.getSensorId().equals(sensorId))
    			tempSensor = iterSensor;
    	
    	this.mySensors.remove(tempSensor);
    	tempSensor = null;
    }
    
    public JSONArray sensorsToJSONArray() {
    	JSONArray temp = new JSONArray();
    	
    	System.out.println(this.mySensors.size());
    	
    	for (int i = 0; i < this.mySensors.size(); i++) {
    		try {
				temp.put(i, this.mySensors.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    		
    	return temp;
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
