// @flow
import axios from 'axios';
import Paper from 'material-ui/Paper';
import RaisedButton from 'material-ui/RaisedButton';
import React, { Component } from 'react';

function stopSimulation() {
  axios.get("/stopSimulation");
  alert('Simulation stopped.');
}


class InputForm extends Component {

  // types
  state : {
    inputDataType:      string,
    startingValue:      string,
    maxNegSpike:        string,
    maxPosSpike:        string,
    dY:                 string,
    anomalyProbability: string,
    frequency:          string,
    sensorId:           string,
    untouched:          boolean
  }

  handleChange : Function
  // end of types

  constructor() {
      super();
      this.state = {
        inputDataType:      'boolean',
        startingValue:      '24',
        maxNegSpike:        '80',
        maxPosSpike:        '80',
        dY:                 '1',
        anomalyProbability: '10',
        frequency:          '1000',
        sensorId:           'mySensor',
        untouched:          true
      };
    }


  startSimulation = () => {
    const inputDataType      : string = "inputDataType="      + this.state.inputDataType;
    const startingValue      : string = "startingValue="      + this.state.startingValue;
    const maxNegSpike        : string = "maxNegSpike="        + this.state.maxNegSpike;
    const maxPosSpike        : string = "maxPosSpike="        + this.state.maxPosSpike;
    const dY                 : string = "dY="                 + this.state.dY;
    const anomalyProbability : string = "anomalyProbability=" + this.state.anomalyProbability;
    const frequency          : string = "frequency="          + this.state.frequency;
    const sensorId           : string = "sensorId="           + this.state.sensorId;

    const request : string = "/createSensor?" + inputDataType          + "&"
                                                  + startingValue      + "&"
                                                  + maxNegSpike        + "&"
                                                  + maxPosSpike        + "&"
                                                  + dY                 + "&"
                                                  + anomalyProbability + "&"
                                                  + frequency          + "&"
                                                  + sensorId;

    axios.get(request)
      .then((response) => {
        this.props.handleSensorUpdate(response.data);
      });

    this.setState({
      untouched: true
    });
  }


  changeSimulation = () => {
    const inputDataType      : string = "inputDataType="      + this.state.inputDataType;
    const startingValue      : string = "startingValue="      + this.state.startingValue;
    const maxNegSpike        : string = "maxNegSpike="        + this.state.maxNegSpike;
    const maxPosSpike        : string = "maxPosSpike="        + this.state.maxPosSpike;
    const dY                 : string = "dY="                 + this.state.dY;
    const anomalyProbability : string = "anomalyProbability=" + this.state.anomalyProbability;
    const frequency          : string = "frequency="          + this.state.frequency;
    const oldSensorId        : string = "oldSensorId="        + this.props.selectedSensorId;
    const newSensorId        : string = "newSensorId="        + this.state.sensorId;

    const request : string = "/updateSensor?" + inputDataType          + "&"
                                                  + startingValue      + "&"
                                                  + maxNegSpike        + "&"
                                                  + maxPosSpike        + "&"
                                                  + dY                 + "&"
                                                  + anomalyProbability + "&"
                                                  + frequency          + "&"
                                                  + oldSensorId        + "&"
                                                  + newSensorId;

    axios.get(request)
      .then((response) => {
        this.props.handleSensorUpdate(response.data);
      });
    
    this.setState({
      untouched: true
    });
  }


  handleChange = (event : { target : {
                            name : string,
                            value : string
                          }}) => {

    switch (event.target.name) {
      case "inputDataType":
        this.setState({inputDataType: event.target.value});
        break;
      case "startingValue":
        this.setState({startingValue: event.target.value});
        break;
      case "maxNegSpike":
        this.setState({maxNegSpike: event.target.value});
        break;
      case "maxPosSpike":
        this.setState({maxPosSpike: event.target.value});
        break;
      case "dY":
        this.setState({dY: event.target.value});
        break;
      case "anomalyProbability":
        this.setState({anomalyProbability: event.target.value});
        break;
      case "frequency":
        this.setState({frequency: event.target.value});
        break;
      case "sensorId":
        this.setState({sensorId: event.target.value});
        break;
      default:
        break;
    }

    if (this.props.selectedSensorId !== '')
      this.setState({
        untouched: false
      });
  }

  render() {
    if (this.props.selectedSensorId === '' && this.state.untouched == false)
      this.setState({
        untouched: true
      });

    const isSensorSelected = this.props.selectedSensorId !== '';
    const isBoolean        = this.state.inputDataType   === 'boolean';

    let renderValues = null;

    // check if there is a sensor selected
    // if (isSensorSelected && this.state.untouched && this.props.selectedSensorId !== this.state.sensorId) {
    if (isSensorSelected && this.state.untouched )
      renderValues = this.props.sensors.find((iterSensor) =>  {
        return this.props.selectedSensorId === iterSensor.sensorId
      });
    else
      renderValues = this.state;


    return (
      <div>
        <form className="center-block"> 
          <div > Input Datatype: </div>
          <select name="inputDataType" value={renderValues.inputDataType} onChange={this.handleChange}>
            <option value="int">Integer</option>
            <option value="boolean">Boolean</option>
            <option value="float">Float</option>
          </select>
          <br/>

          {!isBoolean &&
            <div>
              {isBoolean}
              <div >
                Start Value:
              </div>
              <input  type="text" name="startingValue" value={renderValues.startingValue} onChange={this.handleChange}>
              </input>
              <br/>

              <div >
                Maximum Negative Spike:
              </div>

              <input  type="text" name="maxNegSpike" value={renderValues.maxNegSpike} onChange={this.handleChange}></input>
              <br/>

              <div >
                Maximum Positive Spike:</div>
              <input  type="text" name="maxPosSpike" value={renderValues.maxPosSpike} onChange={this.handleChange}></input>
              <br/>

              <div >
                Interval change:</div>

              <input  type="text" name="dY" value={renderValues.dY} onChange={this.handleChange}></input>
              <br/>

              <div >
                Anomaly Probability:
              </div>
              <input  type="text" name="anomalyProbability" value={renderValues.anomalyProbability} onChange={this.handleChange}></input>
              <br/>
            </div>
          } 
          <div >
            Interval (ms):
          </div>
          <input  type="text" name="frequency" value={renderValues.frequency} onChange={this.handleChange}></input>

          <div >
            Sensor-ID:
          </div>
          <input  type="text" name="sensorId" value={renderValues.sensorId} onChange={this.handleChange}></input>
        </form>


        {isSensorSelected ? (
          <RaisedButton onClick={this.changeSimulation} label="Update Sensor"secondary={true} />
        ) : (
          <RaisedButton onClick={this.startSimulation} label="Create Sensor" primary={true} />
        )}
        <br/>
      </div>
    );
  }
}

export default InputForm;
