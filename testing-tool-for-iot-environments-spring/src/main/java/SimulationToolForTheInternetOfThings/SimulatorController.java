package SimulationToolForTheInternetOfThings;

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


    @RequestMapping("/startSimulation")
    @ResponseBody
    public void startSimulation(String inputDataType, String startingValue, String maxNegSpike, String maxPosSpike, String dY, String anomalyProbability, String frequency) {
        if (controllerThreadManager != null)
            stopSimulation();

        SensorThreadClock threadClock = new SensorThreadClock(Integer.parseInt(frequency));
        controllerThreadManager = new ThreadManager(Integer.parseInt(frequency));



        // TODO: add numberOfThreads as parameter
        int numberOfThreads = 5;
        // TODO: add broker as parameter
        String broker = "tcp://localhost:1883";

        controllerThreadManager.setNumberOfThreads(numberOfThreads);
        controllerThreadManager.setBroker(broker);

        for (int i = 0; i < numberOfThreads; i++) {
            controllerThreadManager.startNewThread( inputDataType,
                    startingValue,
                    maxNegSpike,
                    maxPosSpike,
                    dY,
                    Float.parseFloat(anomalyProbability),
                    Integer.parseInt(frequency),
                    i,
                    threadClock);
        }
    }


    @RequestMapping("/changeSimulation")
    @ResponseBody
    public void changeSimulation(String inputDataType, String startingValue, String maxNegSpike, String maxPosSpike, String dY, String anomalyProbability, String frequency) {
        controllerThreadManager.changeThreads(inputDataType,
                startingValue,
                maxNegSpike,
                maxPosSpike,
                dY,
                Float.parseFloat(anomalyProbability),
                Integer.parseInt(frequency));
    }

    @RequestMapping("/stopSimulation")
    @ResponseBody
    public void stopSimulation() {
        controllerThreadManager.stopAllThreads();
    }

    @RequestMapping("/setBroker")
    @ResponseBody
    public void setBroker(String broker) {
        controllerThreadManager.setBroker(broker);
    }
}