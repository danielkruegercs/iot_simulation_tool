// @flow
import React, { Component } from 'react';
import axios from 'axios';


function stopSimulation() {
  axios.get("/stopSimulation");
  alert('Simulation stopped.');
}


class App extends Component {

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
        inputDataType:      '',
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
      default:
        break;
    }
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <h2>Testing Tool For IoT Environments</h2>
        </div>

        <form>
         <p className="oneone"> Input Datatype: </p> <input className="twoone" type="text" name="inputDataType" onChange={this.handleChange}></input>
         <br/>
         <p className="onetwo"> Start Value:  </p> <input className="twotwo" type="text" name="startingValue" onChange={this.handleChange}></input>
         <br/>
         <p className="onethree"> Maximum Negative Spike:  </p> <input className="twothree" type="text" name="maxNegSpike" onChange={this.handleChange}></input>
         <br/>
         <p className="onefour"> Maximum Positive Spike: </p>  <input className="twofour" type="text" name="maxPosSpike" onChange={this.handleChange}></input>
         <br/>
         <p className="onefive"> Interval change: </p>  <input className="twofive" type="text" name="dY" onChange={this.handleChange}></input>
         <br/>
         <p className="onesix"> Anomaly Probability:  </p> <input className="twosix" type="text" name="anomalyProbability" onChange={this.handleChange}></input>
         <br/>
         <p className="oneseven"> Frequency: </p>  <input className="twoseven" type="text" name="frequency" onChange={this.handleChange}></input>
        </form>


        <button onClick={this.startSimulation}>Start Simulation</button>
        <br/>
        <button onClick={this.changeSimulation}>Change Simulation</button>
        <br/>
        <button onClick={stopSimulation}>Stop Simulation</button>
      </div>
    );
  }
}

export default App;
