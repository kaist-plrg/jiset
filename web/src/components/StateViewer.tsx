import React from "react";
import {
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Icon,
  Typography,
} from "@material-ui/core";
import "../styles/StateViewer.css";

import StackFrameViewer from "./StackFrameViewer";
import HeapViewer from "./HeapViewer";
import Breakpoints from "./Breakpoints";
import SpecEnvViewer from "./SpecEnvViewer";
import JSEnvViewer from "./JSEnvViewer";
import JSBreakpoints from "./JSBreakpoints";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { AppState } from "../controller/AppState";

// State viewer item
type StateViewerItemProps = {
  disabled: boolean;
  header: React.ReactElement;
  headerStyle?: object;
  body: React.ReactElement;
  bodyStyle?: object;
};
type StateViewerItemState = {
  expanded: boolean;
};
class StateViewerItem extends React.Component<
  StateViewerItemProps,
  StateViewerItemState
> {
  constructor ( props: StateViewerItemProps ) {
    super( props );
    this.state = { expanded: false };
  }
  componentDidUpdate ( prev: StateViewerItemProps ) {
    // close accordian when diasabled
    if ( !prev.disabled && this.props.disabled )
      this.setState( { ...this.state, expanded: false } );
  }
  onItemClick () {
    const { disabled } = this.props;
    if ( !disabled ) {
      let expanded = !this.state.expanded;
      this.setState( { ...this.state, expanded } );
    }
  }
  render () {
    const { disabled, header, headerStyle, body, bodyStyle } = this.props;
    const { expanded } = this.state;
    return (
      <Accordion expanded={ expanded } disabled={ disabled }>
        <AccordionSummary
          onClick={ () => this.onItemClick() }
          expandIcon={ <Icon>expand_more</Icon> }
          style={ headerStyle }
        >
          { header }
        </AccordionSummary>
        <AccordionDetails style={ bodyStyle }>{ body }</AccordionDetails>
      </Accordion>
    );
  }
}

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  disableStateViewer: !(
    st.controller.state === AppState.DEBUG_READY ||
    st.controller.state === AppState.TERMINATED
  ),
} );
const connector = connect( mapStateToProps );
type StateViewerProps = ConnectedProps<typeof connector>;

class StateViewer extends React.Component<StateViewerProps> {
  render () {
    const { disableStateViewer } = this.props;

    return (
      <div className="spec-state-viewer-container">
        <StateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>ECMAScript Call Stack</Typography> }
          body={ <StackFrameViewer /> }
        />
        <StateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>ECMAScript Environment</Typography> }
          body={ <SpecEnvViewer /> }
        />
        <StateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>ECMAScript Heap</Typography> }
          bodyStyle={ { paddingTop: 0 } }
          body={ <HeapViewer /> }
        />
        <StateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>ECMAScript Breakpoints</Typography> }
          body={ <Breakpoints /> }
        />
        <StateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>JavaScript Environment</Typography> }
          body={ <JSEnvViewer /> }
        />
        <StateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>JavaScript Breakpoints</Typography> }
          body={ <JSBreakpoints /> }
        />
      </div>
    );
  }
}

export default connector( StateViewer );
