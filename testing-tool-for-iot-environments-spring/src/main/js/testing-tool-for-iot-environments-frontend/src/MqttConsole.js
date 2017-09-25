// @flow
import {List, ListItem} from 'material-ui/List';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React, { Component } from 'react';
var mqtt = require('mqtt')

let client = mqtt.connect('tcp://localhost:1884')


class MqttConsole extends Component {
  state: {
    messages: Array<string>,
    oldSensorId: string
  }


  constructor (props: Object) {
    super(props);

    this.state = {
      messages: [],
      oldSensorId: ''
    };
    

    client.on('message', (topic: string, message: string) => {
      let tempMessages = this.state.messages; 

      if (tempMessages.length === 3)
        tempMessages = tempMessages.slice(1);
      
      tempMessages.push(message.toString());

      this.setState({
        messages: tempMessages
      });
    })
  }

  render() {
    if (this.props.selectedSensorId !== this.state.oldSensorId) {
      client.unsubscribe(this.state.oldSensorId);
      client.subscribe(this.props.selectedSensorId);
      this.setState({
        oldSensorId: this.props.selectedSensorId,
        messages: []
      });
    }
    
    return (
      <MuiThemeProvider >
        <List >
          {
            this.state.messages.map((iterMessage) => {
              const iterKey = this.state.messages.indexOf(iterMessage); 
              return (<ListItem key={iterKey} primaryText={iterMessage} />);
            })
          }
        </List>
      </MuiThemeProvider>
    );
  }
}

export default MqttConsole;
