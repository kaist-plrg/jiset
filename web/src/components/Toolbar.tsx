import React from "react";
import { Button, ButtonGroup } from "@material-ui/core";
import "../styles/Toolbar.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState, Dispatch } from "../store";

import { AppState } from "../store/reducers/AppState";
import { run, stop, specStep, specStepOut, specStepOver, jsStep, jsStepOut, jsStepOver, specContinue } from "../store/reducers/Debugger";

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
  jsStep: () => dispatch(jsStep()),
  jsStepOut: () => dispatch(jsStepOut()),
  jsStepOver: () => dispatch(jsStepOver()),
  specContinue: () => dispatch(specContinue()),
});
const connector = connect( mapStateToProps, mapDispatchToProps );
type ToolbarProps = ConnectedProps<typeof connector>;

class Toolbar extends React.Component<ToolbarProps> {
  render () {
    const { disableRun, disableDebuggerBtn, run, stop, specStep, specStepOver, specStepOut, jsStep, jsStepOver, jsStepOut } = this.props;
    return (
      <div className="toolbar-container">
        <ButtonGroup variant="text" color="primary">
          <Button disabled={ disableRun } onClick={ () => run() }>Run</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => stop() }>Cancel</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => specStep() }>Step</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => specStepOver() }>Step-Over</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => specStepOut() }>Step-Out</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => jsStep() }>Js-Step</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => jsStepOver() }>Js-Step-Over</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => jsStepOut() }>Js-Step-Out</Button>
          <Button disabled={ disableDebuggerBtn } onClick={ () => specContinue() }>Continue</Button>
        </ButtonGroup>
      </div>
    );
  }
}

export default connector( Toolbar );
