package SimulationToolForTheInternetOfThings;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.json.*;


/**
 * Created by Daniel Krueger on 14-Jun-17.
 */
public class SensorSimulatorThread extends Thread {

    // beginning of attributes
    private String inputDatatype    = "";

    int startingValueInt            = 0;
    float startingValueFloat        = 0.0f;
    boolean startingValueBoolean    = false;

    int maxNegSpikeInt              = 0;
    int maxPosSpikeInt              = 0;
    float maxNegSpikeFloat          = 0.0f;
    float maxPosSpikeFloat          = 0.0f;

    int dyInt                       = 0;
    float dyFloat                   = 0.0f;

    float anomalyProbability        = 0.0f;
    int frequency                   = 500;

    int valueInt                    = 0;
    float valueFloat                = 0.0f;
    boolean valueBoolean            = false;


    private String threadId         = "";
    private boolean stopRequested   = false;


    // MQTT attribute
    private MqttClient sampleClient = null;
    private String topic            = "";
    private int qos                 = -1;
    private String broker           = "";
    private String clientId         = "";
    MemoryPersistence persistence   = null;
    // end of MQTT attribute
    // end of attributes

    

    // beginning of getters and setters
    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }
    // end of getters and setters


    public SensorSimulatorThread(String inputDatatype,
                                 String startingValue,
                                 String maxNegSpike,
                                 String maxPosSpike,
                                 String dY,
                                 float anomalyProbability,
                                 int frequency,
                                 String threadId) {

        this.threadId      = threadId;
        this.inputDatatype = inputDatatype;
        this.frequency     = frequency;
        
        

        if (inputDatatype.equals(new String("int"))) {
            this.startingValueInt = Integer.parseInt(startingValue);
            this.maxNegSpikeInt = Integer.parseInt(maxNegSpike);
            this.maxPosSpikeInt = Integer.parseInt(maxPosSpike);
            this.dyInt = Integer.parseInt(dY);
            this.anomalyProbability = anomalyProbability;
        }
        else if (inputDatatype.equals(new String("float"))) {
            this.startingValueFloat = Float.parseFloat(startingValue);
            this.maxNegSpikeFloat = Float.parseFloat(maxNegSpike);
            this.maxPosSpikeFloat = Float.parseFloat(maxPosSpike);
            this.dyFloat = Float.parseFloat(dY);
            this.anomalyProbability = anomalyProbability;
        }

        this.broker         = "tcp://localhost:1883";
        this.topic          = this.threadId;
        String mqttContent  = "Message from MqttPublishSample: Thread " + this.threadId;
        this.qos            = 2;
        this.clientId       = "JavaSample_Thread_" + this.threadId;
        persistence         = new MemoryPersistence();
    }

    private int generateInt() {
        float anomalyHappeningProbability = (float) ThreadLocalRandom.current().nextDouble(0, 100);

        if (anomalyHappeningProbability < anomalyProbability)
            return ThreadLocalRandom.current().nextInt(valueInt - maxNegSpikeInt, valueInt + maxPosSpikeInt + 1);
        else {
            if (new Random().nextBoolean())
                valueInt += dyInt;
            else
                valueInt -= dyInt;

            return valueInt;
        }
    }

    private float generateFloat() {
        float anomalyHappeningProbability = (float) ThreadLocalRandom.current().nextDouble(0, 100);

        if (anomalyHappeningProbability < anomalyProbability) {
            double min = (double) valueFloat - (double) maxNegSpikeFloat;
            double max = (double) valueFloat + (double) maxPosSpikeFloat + 1.0;

            return (float) ThreadLocalRandom.current().nextDouble(min, max);
        } else {
            if (new Random().nextBoolean())
                valueFloat += dyFloat;
            else
                valueFloat -= dyFloat;

            return valueFloat;
        }
    }

    private static boolean generateBoolean() {
        return new Random().nextBoolean();
    }

    private static void nanoWait(long startTime) {
        long end=0;
        do{
            end = System.nanoTime();
        }while(startTime - end >= 0);
    }


    public void run() {
        valueInt     = startingValueInt;
        valueFloat   = startingValueFloat;
        valueBoolean = startingValueBoolean;

        while (!this.stopRequested) {
            JSONObject content = new JSONObject();

            try {
				content.put("time", System.currentTimeMillis());

	            if (inputDatatype.equals(new String("int")))
	                content.put("value", this.generateInt());
	            else if (inputDatatype.equals(new String("float")))
	                content.put("value", this.generateFloat());
	            else if (inputDatatype.equals(new String("boolean")))
	                content.put("value", this.generateBoolean());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            try {
            	sampleClient = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                sampleClient.connect(connOpts);

                MqttMessage message = new MqttMessage(content.toString().getBytes());
                
                message.setQos(qos);
                this.sampleClient.publish(topic, message);
                
                this.sampleClient.disconnect();
            } catch (MqttException me) {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }


            try {
                Thread.sleep(frequency);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}