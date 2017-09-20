// @flow
import {List, ListItem} from 'material-ui/List';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React, { Component } from 'react';


class MqttConsole extends Component {

  render() {
    return (
      <MuiThemeProvider >
        <List>
          <ListItem primaryText="Mqtt-Message" />
        </List>
      </MuiThemeProvider>
    );
  }
}

export default MqttConsole;
