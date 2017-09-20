// @flow
import axios from 'axios';
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
    frequency:          string
  }

  handleChange : Function
  // end of types

  constructor() {
      super();
      this.state = {
        inputDataType:      'int',
        startingValue:      '',
        maxNegSpike:        '',
        maxPosSpike:        '',
        dY:                 '',
        anomalyProbability: '',
        frequency:          ''
      };

      this.handleChange = this.handleChange.bind(this);
    }


  startSimulation = () => {
    const inputDataType      : string = "inputDataType="      + this.state.inputDataType;
    const startingValue      : string = "startingValue="      + this.state.startingValue;
    const maxNegSpike        : string = "maxNegSpike="        + this.state.maxNegSpike;
    const maxPosSpike        : string = "maxPosSpike="        + this.state.maxPosSpike;
    const dY                 : string = "dY="                 + this.state.dY;
    const anomalyProbability : string = "anomalyProbability=" + this.state.anomalyProbability;
    const frequency          : string = "frequency="          + this.state.frequency;

    const request : string = "/startSimulation?" + inputDataType      + "&"
                                                  + startingValue      + "&"
                                                  + maxNegSpike        + "&"
                                                  + maxPosSpike        + "&"
                                                  + dY                 + "&"
                                                  + anomalyProbability + "&"
                                                  + frequency

    axios.get(request);
    alert('Simulation started.');
  }


  changeSimulation = () => {
    const inputDataType      : string = "inputDataType="      + this.state.inputDataType;
    const startingValue      : string = "startingValue="      + this.state.startingValue;
    const maxNegSpike        : string = "maxNegSpike="        + this.state.maxNegSpike;
    const maxPosSpike        : string = "maxPosSpike="        + this.state.maxPosSpike;
    const dY                 : string = "dY="                 + this.state.dY;
    const anomalyProbability : string = "anomalyProbability=" + this.state.anomalyProbability;
    const frequency          : string = "frequency="          + this.state.frequency;

    const request : string = "/changeSimulation?" + inputDataType      + "&"
                                                  + startingValue      + "&"
                                                  + maxNegSpike        + "&"
                                                  + maxPosSpike        + "&"
                                                  + dY                 + "&"
                                                  + anomalyProbability + "&"
                                                  + frequency

    axios.get(request);
    alert('Simulation started.');
  }


  handleChange(event : { target : {
                            name : string,
                            value : string
                          }}) {

    console.log(event.target);
    console.log(event);
    switch (event.target.name) {
      case "inputDataType":
        console.log(event.target.value);
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
      default:
        break;
    }
  }

  render() {
    return (
      <div>
        <form className="center-block"> 
          <div > Input Datatype: </div>
          <select name="inputDataType" value={this.state.inputDataType} onChange={this.handleChange}>
            <option value="int">Integer</option>
            <option value="boolean">Boolean</option>
            <option value="float">Float</option>
          </select>
          <br/>

          <div >
            Start Value:
          </div>
          <input  type="text" name="startingValue" onChange={this.handleChange}>
          </input>
          <br/>

          <div >
            Maximum Negative Spike:
          </div>
          <input  type="text" name="maxNegSpike" onChange={this.handleChange}></input>
          <br/>

          <div >
            Maximum Positive Spike:</div>
          <input  type="text" name="maxPosSpike" onChange={this.handleChange}></input>
          <br/>

          <div >
            Interval change:</div>
          <input  type="text" name="dY" onChange={this.handleChange}></input>
          <br/>

          <div >
            Anomaly Probability:
          </div>
          <input  type="text" name="anomalyProbability" onChange={this.handleChange}></input>
          <br/>

          <div >
            Frequency:
          </div>
          <input  type="text" name="frequency" onChange={this.handleChange}></input>
        </form>


        <button onClick={this.startSimulation} className="btn-success">Start Simulation</button>
        <br/>
    
        <button onClick={this.changeSimulation} className="btn-info">Change Simulation</button>
        <br/>

        <button onClick={stopSimulation} className="btn-danger">Stop Simulation</button>
     </div>
    );
  }
}

export default InputForm;
