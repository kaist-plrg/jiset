import React from "react";
import EnvViewer from "./EnvViewer";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  stackFrame: st.ir.stackFrame,
} );
const connector = connect( mapStateToProps );
type JSEnvViewerProps = ConnectedProps<typeof connector>;

class JSEnvViewer extends React.Component<JSEnvViewerProps> {
  render () {
    const { stackFrame } = this.props;
    let env: [ string, string ][] = [];
    // TODO Fill out
    // if ( stackFrame.data.length > 0 ) {
    //   env = stackFrame.data[ stackFrame.idx ][ 2 ];
    //   // filter temporary variable
    //   env = env.filter( ( [ name, _ ] ) => !( name.startsWith( "__" ) && name.endsWith( "__" ) ) )
    // }
    return (
      <EnvViewer env={ env } />
    );
  }
}

export default connector( JSEnvViewer );
