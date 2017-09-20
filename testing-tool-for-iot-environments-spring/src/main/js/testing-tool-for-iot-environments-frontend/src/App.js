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

  handleSensorInput = (newSensorData : SensorDataObject): void =>  {
    let updatedSensors = this.state.sensors;
    updatedSensors.map((iterSensor) => {
      if (iterSensor.sensorId === this.state.selectedSensorId)
        iterSensor.sensorData = newSensorData;
    });

    this.setState({
      sensors: updatedSensors
    });
  }

  handleSensorCreate = (newSensor: Sensor): void =>  {
    let updatedSensors = this.state.sensors;
    updatedSensors.push(newSensor);

    this.setState({
      sensors: updatedSensors
    });
  }


  handleSensorDelete = (sensorId: string): void =>  {
    let updatedSensors = this.state.sensors;
    updatedSensors.map((iterSensor) => (iterSensor.sensorId !== sensorId));

    this.setState({
      sensors: updatedSensors
    });
  }

  render() {
    return (
      <div className="container-fluid">
        <div className="row">
          <h2>Testing Tool For IoT Environments</h2>
        </div>

        <div className="row">
          <div className="col-lg-4" style={{borderStyle: 'solid'}}>
            <SensorList  sensorList={this.state.sensors}
              selectedSensorId={this.state.selectedSensorId}
              handleSensorCreate={this.handleSensorCreate}
              handleSensorDelete={this.handleSensorDelete} />
          </div>
          <div className="col-lg-4" style={{borderStyle: 'solid'}}>
            <InputForm onSubmit={this.handleSensorInput} />
          </div>
          <div className="col-lg-4" style={{borderStyle: 'solid'}}>
            <MqttConsole sensorList={this.state.sensors} selectedSensorId={this.state.selectedSensorId} />
          </div>

        </div>
      </div>
    );
  }
}

export default App;
