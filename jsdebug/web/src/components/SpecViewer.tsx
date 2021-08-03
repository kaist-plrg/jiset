import React from "react";
import { Typography, Paper } from "@material-ui/core";
import AlgoViewer from "./AlgoViewer";
import { getAlgo } from "../object/Spec";
import { Algo } from "../object/Algo";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  stackFrame: st.ir.stackFrame,
  spec: st.spec.spec,
} );
const connector = connect( mapStateToProps );
type SpecViewerProps = ConnectedProps<typeof connector>;

class SpecViewer extends React.Component<SpecViewerProps> {
  render () {
    const { stackFrame, spec } = this.props;
    let algo: Algo | undefined;
    let currentStep: number;
    let algoName: string;

    if ( stackFrame.data.length === 0 ) {
      algo = undefined;
      currentStep = -1;
    } else {
      [ algoName, currentStep ] = stackFrame.data[ stackFrame.idx ];
      algo = getAlgo( spec, algoName );
      if ( currentStep === undefined ) currentStep = -1;
    }

    return (
      <Paper className="spec-viewer-container" variant="outlined">
        <Typography variant="h6">ECMAScript Specification</Typography>
        <AlgoViewer data={ algo } currentStep={ currentStep } />
      </Paper>
    );
  }
}

export default connector( SpecViewer );
