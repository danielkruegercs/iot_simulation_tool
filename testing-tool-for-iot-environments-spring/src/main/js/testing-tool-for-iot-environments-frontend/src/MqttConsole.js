// @flow
import {List, ListItem} from 'material-ui/List';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React, { Component } from 'react';
var mqtt = require('mqtt')

let client = mqtt.connect('tcp://localhost:1884')


class MqttConsole extends Component {
  state: {
    messages: Array<string>
  }


  constructor (props: Object) {
    super(props);



    this.state = {
      messages: []
    };
    
    client.on('connect', () => {
      //client.subscribe(props.selectedSensorId);
      client.subscribe('asdf');
    })


    client.on('message', (topic: string, message: string) => {
      let tempMessages = this.state.messages; 



      tempMessages.push(message.toString());



      this.setState({
        messages: tempMessages
      });
    })
  }

  render() {

    return (
      <MuiThemeProvider >
      <List>
        {
          this.state.messages.map((iterMessage) => (
            <ListItem primaryText={iterMessage} />
          ))
        }
      </List>
      </MuiThemeProvider>
    );
  }
}

export default MqttConsole;
