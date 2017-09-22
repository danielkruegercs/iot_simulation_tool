package SimulationToolForTheInternetOfThings;

import org.json.JSONException;
import org.json.JSONObject;

public class Sensor {
	private String sensorId;
	private String inputDatatype;
	private String startingValue;
    private String maxNegSpike;
    private String maxPosSpike;
    private String dY;
    private float anomalyProbability;
    private int frequency;
	
    public String getSensorId() {
		return sensorId;
	}
    
    public String getInputDatatype() {
		return inputDatatype;
	}


	public String getStartingValue() {
		return startingValue;
	}


	public String getMaxNegSpike() {
		return maxNegSpike;
	}


	public String getMaxPosSpike() {
		return maxPosSpike;
	}


	public String getdY() {
		return dY;
	}


	public float getAnomalyProbability() {
		return anomalyProbability;
	}


	public int getFrequency() {
		return frequency;
	}


    
    public Sensor(	String inputDatatype,
					String startingValue,
					String maxNegSpike,
					String maxPosSpike,
					String dY,
					float anomalyProbability,
					int frequency,
					String sensorId) {
    	this.sensorId = sensorId;
    	this.inputDatatype = inputDatatype;
    	this.startingValue = startingValue;
    	this.maxNegSpike = maxNegSpike;
    	this.maxPosSpike = maxPosSpike;
    	this.dY = dY;
    	this.anomalyProbability = anomalyProbability;
    	this.frequency = frequency;
    }
    
	public JSONObject toJson() {
		JSONObject sensorObject = new JSONObject();
		try {
			sensorObject.put("sensorId", this.sensorId);
			sensorObject.put("inputDatatype", this.inputDatatype);
			sensorObject.put("startingValue", this.startingValue);
			sensorObject.put("maxNegSpike", this.maxNegSpike);
			sensorObject.put("maxPosSpike", this.maxPosSpike);
			sensorObject.put("dY", this.dY);
			sensorObject.put("anomalyProbability", this.anomalyProbability);
			sensorObject.put("frequency", this.frequency);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sensorObject;
	}
}
