// @flow
import axios from 'axios';
import FontIcon from 'material-ui/FontIcon';
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
    e.stopPropagation();

    const sensorId: string = "sensorId=" + e.target.parentElement.textContent.replace(e.target.textContent, '');
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
              const iterKey = this.props.sensors.indexOf(iterSensor);
              
              if (iterSensor.sensorId === this.props.selectedSensorId)
                return (<ListItem key={iterKey} style={{backgroundColor: 'green'}} 
                                  onClick={this.handleClick} 
                                  rightIconButton={<FontIcon sensorId={iterSensor.sensorId}
                                                             hoverColor={'lightgrey'}
                                                             className="material-icons" 
                                                             onClick={this.handleDoubleClick}>close</FontIcon>} 
                                  primaryText={iterSensor.sensorId} />);
              else
                return (<ListItem key={iterKey} onClick={this.handleClick} 
                                  rightIconButton={<FontIcon sensorId={iterSensor.sensorId}
                                                             hoverColor={'lightgrey'}
                                                             className="material-icons" 
                                                             onClick={this.handleDoubleClick}>close</FontIcon>} 
                                  primaryText={iterSensor.sensorId} />);
            })
          }
        </List>
      </MuiThemeProvider>
    );
  }
}

export default SensorList;
