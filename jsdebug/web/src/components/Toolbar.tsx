import React from "react";
import { Button, ButtonGroup } from "@material-ui/core";
import "../styles/Toolbar.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { AppState } from "../controller/AppState";
import { ActionType } from "../controller/Action";
import sm from "../controller";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  disableRun: st.controller.state !== AppState.JS_INPUT,
  disableDebuggerBtn: st.controller.state !== AppState.DEBUG_READY,
} );
const connector = connect( mapStateToProps );
type ToolbarProps = ConnectedProps<typeof connector>;

class Toolbar extends React.Component<ToolbarProps> {
  onRunButtonClick () {
    sm.move( { type: ActionType.START_DBG } );
  }

  onCancelButtonClick () {
    sm.move( { type: ActionType.STOP_DBG } );
  }

  onStepButtonClick () {
    sm.move( { type: ActionType.STEP } );
  }

  onStepOverButtonClick () {
    sm.move( { type: ActionType.STEP_OVER } );
  }

  onStepOutButtonClick () {
    sm.move( { type: ActionType.STEP_OUT } );
  }

  onStepLineButtonClick () {
    sm.move( { type: ActionType.STEP_LINE } );
  }

  onContinueButtonClick () {
    sm.move( { type: ActionType.CONTINUE } );
  }

  render () {
    const { disableRun, disableDebuggerBtn } = this.props;
    return (
      <div className="toolbar-container">
        <ButtonGroup variant="text" color="primary">
          <Button disabled={ disableRun } onClick={ () => this.onRunButtonClick() }>Run</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => this.onCancelButtonClick() }>Cancel</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => this.onStepButtonClick() }>Step</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => this.onStepOverButtonClick() }>Step-Over</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => this.onStepOutButtonClick() }>Step-Out</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => this.onStepLineButtonClick() }>Step-Line</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => this.onContinueButtonClick() }>Continue</Button>
        </ButtonGroup>
      </div>
    );
  }
}

export default connector( Toolbar );
