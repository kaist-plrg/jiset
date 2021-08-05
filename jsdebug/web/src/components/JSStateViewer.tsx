import React from "react";
import {
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Icon,
  Typography,
} from "@material-ui/core";
import "../styles/StateViewer.css";

import Breakpoints from "./Breakpoints";
import JSEnvViewer from "./JSEnvViewer";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { AppState } from "../controller/AppState";

// Spec State viewer item
type JSStateViewerItemProps = {
  disabled: boolean;
  header: React.ReactElement;
  headerStyle?: object;
  body: React.ReactElement;
  bodyStyle?: object;
};
type JSStateViewerItemState = {
  expanded: boolean;
};
class JSStateViewerItem extends React.Component<
  JSStateViewerItemProps,
  JSStateViewerItemState
> {
  constructor ( props: JSStateViewerItemProps ) {
    super( props );
    this.state = { expanded: false };
  }
  componentDidUpdate ( prev: JSStateViewerItemProps ) {
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
type JSStateViewerProps = ConnectedProps<typeof connector>;

class JSStateViewer extends React.Component<JSStateViewerProps> {
  render () {
    const { disableStateViewer } = this.props;

    return (
      <div className="spec-state-viewer-container">
        <JSStateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>JavaScript Environment</Typography> }
          body={ <JSEnvViewer /> }
        />
        <JSStateViewerItem
          disabled={ disableStateViewer }
          header={ <Typography>JavaScript Breakpoints</Typography> }
          body={ <Breakpoints /> }
        />
      </div>
    );
  }
}

export default connector( JSStateViewer );
