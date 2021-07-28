import React from "react";
import StackFrameViewer from "./StackFrameViewer";
import StateWatcher from "./StateWatcher";
import { Paper } from "@material-ui/core";

class StateViewer extends React.Component {
  render () {
    return (
      <Paper variant="outlined">
        <StateWatcher />
        <StackFrameViewer />
      </Paper>
    );
  }
}

export default StateViewer;
