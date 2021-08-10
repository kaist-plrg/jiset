import React from "react";
import EnvViewer from "./EnvViewer";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  env: st.ir.env,
} );
const connector = connect( mapStateToProps );
type JSEnvViewerProps = ConnectedProps<typeof connector>;

class JSEnvViewer extends React.Component<JSEnvViewerProps> {
  render () {
    const { env } = this.props;
    return (
      <EnvViewer env={ env } />
    );
  }
}

export default connector( JSEnvViewer );
