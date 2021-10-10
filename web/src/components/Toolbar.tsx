import React from "react";
import { Button, ButtonGroup } from "@material-ui/core";
import "../styles/Toolbar.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState, Dispatch } from "../store";

import { AppState } from "../store/reducers/AppState";
import { run, stop, specStep, specStepOut, specStepOver, specContinue } from "../store/reducers/Debugger";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  disableRun: st.appState.state !== AppState.JS_INPUT,
  disableDebuggerBtn: st.appState.state !== AppState.DEBUG_READY,
} );
const mapDispatchToProps = (dispatch : Dispatch) => ( {
  run: () => dispatch(run()),
  stop: () => dispatch(stop()),
  specStep: () => dispatch(specStep()),
  specStepOut: () => dispatch(specStepOut()),
  specStepOver: () => dispatch(specStepOver()),
  specContinue: () => dispatch(specContinue()),
});
const connector = connect( mapStateToProps, mapDispatchToProps );
type ToolbarProps = ConnectedProps<typeof connector>;

class Toolbar extends React.Component<ToolbarProps> {
  onRunButtonClick () {
    this.props.run();
  }

  onCancelButtonClick () {
    this.props.stop();
  }

  onStepButtonClick () {
    this.props.specStep();
  }

  onStepOverButtonClick () {
    this.props.specStepOver();
  }

  onStepOutButtonClick () {
    this.props.specStepOut();
  }

  onStepLineButtonClick () {
    // sm.move( { type: ActionType.STEP_LINE } );
  }

  onContinueButtonClick () {
    this.props.specContinue();
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
