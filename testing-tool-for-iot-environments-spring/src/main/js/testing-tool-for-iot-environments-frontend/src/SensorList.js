// @flow
import {List, ListItem} from 'material-ui/List';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React, { Component } from 'react';


class SensorList extends Component {

  render() {
    return (
      <MuiThemeProvider> 
        <List >
          {
            this.props.sensors.map((iterSensor) => (
              <ListItem primaryText={iterSensor.sensorId} />
            ))
          }
          <ListItem  primaryText="New Sensor"/>
        </List>
      </MuiThemeProvider>
    );
  }
}

export default SensorList;
