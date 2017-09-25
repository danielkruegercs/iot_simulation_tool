// @flow
import axios from 'axios';
import {List, ListItem} from 'material-ui/List';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React, { Component } from 'react';

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

type SensorlistPropsType = {
  sensorList: Array<Sensor>,
  selectedSensorId: string,
  handleSelectedSensor: Function,
  handleSensorCreate: Function,
  handleSensorDelete: Function
}



class SensorList extends Component {

  // handlers
  handleClick = (e: Object) => {
    if (this.props.selectedSensorId === e.target.textContent)
      this.props.handleSelectedSensor('');
    else
      this.props.handleSelectedSensor(e.target.textContent);
  }

  handleDoubleClick = (e: Object) => {
    const sensorId: string = "sensorId=" + e.target.textContent;
    const request : string = "/deleteSensor?" + sensorId;
    
    this.props.handleSelectedSensor('');
    axios.get(request)
      .then((response) => {
        this.props.handleSensorUpdate(response.data);
      });
  }

  render() {
    return (
      <MuiThemeProvider> 
        <List >
          {
            this.props.sensors.map((iterSensor) => {
              
              if (iterSensor.sensorId === this.props.selectedSensorId)
                return (<ListItem style={{backgroundColor: 'green'}} onClick={this.handleClick} onDoubleClick={this.handleDoubleClick} primaryText={iterSensor.sensorId} />);
              else
                return (<ListItem onClick={this.handleClick} onDoubleClick={this.handleDoubleClick} primaryText={iterSensor.sensorId} />);
            })
          }
        </List>
      </MuiThemeProvider>
    );
  }
}

export default SensorList;
