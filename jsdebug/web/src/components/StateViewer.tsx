import React from "react";
import {
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Icon,
  Typography,
} from "@material-ui/core";

import StackFrameViewer from "./StackFrameViewer";
// TODO import StateWatcher from "./StateWatcher";
import Breakpoints from "./Breakpoints";
import SpecEnvViewer from "./SpecEnvViewer";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { AppState } from "../controller/AppState";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  disableStateViewer: st.controller.state !== AppState.DEBUG_READY,
} );
const connector = connect( mapStateToProps );
type StateViewerProps = ConnectedProps<typeof connector>;

class StateViewer extends React.Component<StateViewerProps> {
  render () {
    const { disableStateViewer } = this.props;

    return (
      <div>
        <Accordion disabled={ disableStateViewer }>
          <AccordionSummary expandIcon={ <Icon>expand_more</Icon> }>
            <Typography>ECMAScript Environment</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <SpecEnvViewer />
          </AccordionDetails>
        </Accordion>
        <Accordion disabled={ disableStateViewer }>
          <AccordionSummary expandIcon={ <Icon>expand_more</Icon> }>
            <Typography>Stack Frame</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <StackFrameViewer />
          </AccordionDetails>
        </Accordion>
        <Accordion disabled={ disableStateViewer }>
          <AccordionSummary expandIcon={ <Icon>expand_more</Icon> }>
            <Typography>Breakpoints</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Breakpoints />
          </AccordionDetails>
        </Accordion>
      </div>
    );
  }
}

export default connector( StateViewer );
