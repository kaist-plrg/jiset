import React from "react";
import { Typography, Paper } from "@material-ui/core";
import AlgoViewer from "./AlgoViewer";
import { getAlgo } from "../object/Spec";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  algoName: st.ir.algoName,
  line: st.ir.line,
  spec: st.spec.spec,
} );
const connector = connect( mapStateToProps );
type SpecViewerProps = ConnectedProps<typeof connector>;

class SpecViewer extends React.Component<SpecViewerProps> {
  render () {
    const { algoName, line, spec } = this.props;
    let algo = getAlgo( spec, algoName );

    return (
      <Paper className="spec-viewer-container" variant="outlined">
        <Typography variant="h6">ECMAScript Specification</Typography>
        <AlgoViewer data={ algo } currentStep={ line } />
      </Paper>
    );
  }
}

export default connector( SpecViewer );
