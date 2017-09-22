package SimulationToolForTheInternetOfThings;


import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SimulatorController {

    ThreadManager controllerThreadManager = null;

    @RequestMapping("/")
    public String index() {
    	return "index";
    }


    @RequestMapping("/createSensor")
    @ResponseBody
    public JSONArray createSensor(String inputDataType, String startingValue, String maxNegSpike, String maxPosSpike, String dY, String anomalyProbability, String frequency, String sensorId) {

    	
    	
        SensorThreadClock threadClock = new SensorThreadClock(Integer.parseInt(frequency));
        
        if (this.controllerThreadManager == null)
        	controllerThreadManager = new ThreadManager(Integer.parseInt(frequency));


        // TODO: add numberOfThreads as parameter
        
        // TODO: add broker as parameter

        controllerThreadManager.createSensor( inputDataType,
                startingValue,
                maxNegSpike,
                maxPosSpike,
                dY,
                Float.parseFloat(anomalyProbability),
                Integer.parseInt(frequency),
                sensorId);
        return controllerThreadManager.sensorsToJSONArray();
    }


    @RequestMapping("/updateSensor")
    @ResponseBody
    public JSONArray updateSensor(String inputDataType, String startingValue, String maxNegSpike, String maxPosSpike, String dY, String anomalyProbability, String frequency, String oldSensorId, String newSensorId) {
        controllerThreadManager.updateSensor(inputDataType,
                startingValue,
                maxNegSpike,
                maxPosSpike,
                dY,
                Float.parseFloat(anomalyProbability),
                Integer.parseInt(frequency),
                oldSensorId,
                newSensorId);
        return controllerThreadManager.sensorsToJSONArray();
    }

    @RequestMapping("/deleteSensor")
    @ResponseBody
    public JSONArray deleteSensor(String sensorId) {
        controllerThreadManager.deleteSensor(sensorId);
        return controllerThreadManager.sensorsToJSONArray();
    }

    @RequestMapping("/setBroker")
    @ResponseBody
    public void setBroker(String broker) {
        controllerThreadManager.setBroker(broker);
    }
}