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

class StateViewer extends React.Component {
  render () {
    return (
      <div>
        <Accordion>
          <AccordionSummary expandIcon={ <Icon>expand_more</Icon> }>
            <Typography>Stack Frame</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <StackFrameViewer />
          </AccordionDetails>
        </Accordion>
        <Accordion>
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

export default StateViewer;
