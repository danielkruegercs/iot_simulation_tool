// @flow
import React, { Component } from 'react';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import InputForm from './InputForm';
import SensorList from './SensorList';
import MqttConsole from './MqttConsole';

type SensorDataObject = {
  inputDataType:      string,
  startingValue:      string,
  maxNegSpike:        string,
  maxPosSpike:        string,
  dY:                 string,
  anomalyProbability: string,
  frequency:          string
};

type Sensor = {
  sensorId: string,
  sensorData: SensorDataObject
}

class App extends Component {

  state : {
    selectedSensorId: string,
      sensors : Array<Sensor>
  }

  constructor() {
    super();
    this.state = {
      selectedSensorId: '',
      sensors: []
    };
  }

  // handlers
  handleSensorUpdate = (newSensors: Array<Sensor>): void =>  {
    this.setState({
      sensors: newSensors 
    });
  }
  
  handleSelectedSensor = (selectedSensorId: string): void =>  {
    this.setState({
      selectedSensorId: selectedSensorId 
    });
  }
  // end of handlers

  render() {
    return (
      <div className="container-fluid">
        <div className="row">
          <h2>Testing Tool For IoT Environments</h2>
        </div>

        <div className="row">
          <div className="col-lg-4" style={{borderStyle: 'solid'}}>
            <SensorList sensors={this.state.sensors}
              selectedSensorId={this.state.selectedSensorId}
              handleSelectedSensor={this.handleSelectedSensor}
              handleSensorUpdate={this.handleSensorUpdate} /> 
          </div>
          <div className="col-lg-4" style={{borderStyle: 'solid'}}>
            <InputForm handleSensorUpdate={this.handleSensorUpdate} />
          </div>
          <div className="col-lg-4" style={{borderStyle: 'solid'}}>
            <MqttConsole selectedSensorId={this.state.selectedSensorId} />
          </div>

        </div>
      </div>
    );
  }
}

export default App;
