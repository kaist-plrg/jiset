import React from "react";
import { Button, ButtonGroup } from "@material-ui/core";
import "../styles/Toolbar.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState, Dispatch } from "../store";

import { AppState } from "../store/reducers/AppState";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  disableRun: st.appState.state !== AppState.JS_INPUT,
  disableDebuggerBtn: st.appState.state !== AppState.DEBUG_READY,
} );
const mapDispatchToProps = (dispatch : Dispatch) => ( {
  dispatch
});
const connector = connect( mapStateToProps, mapDispatchToProps );
type ToolbarProps = ConnectedProps<typeof connector>;

class Toolbar extends React.Component<ToolbarProps> {
  onRunButtonClick () {
    // sm.move( { type: ActionType.START_DBG } );
  }

  onCancelButtonClick () {
    // sm.move( { type: ActionType.STOP_DBG } );
  }

  onStepButtonClick () {
    // sm.move( { type: ActionType.STEP } );
  }

  onStepOverButtonClick () {
    // sm.move( { type: ActionType.STEP_OVER } );
  }

  onStepOutButtonClick () {
    // sm.move( { type: ActionType.STEP_OUT } );
  }

  onStepLineButtonClick () {
    // sm.move( { type: ActionType.STEP_LINE } );
  }

  onContinueButtonClick () {
    // sm.move( { type: ActionType.CONTINUE } );
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
