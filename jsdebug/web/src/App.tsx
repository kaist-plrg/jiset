import React from "react";
import { Grid } from "@material-ui/core";
import SpecViewer from "./components/SpecViewer";
import Toolbar from "./components/Toolbar";
import StateViewer from "./components/StateViewer";
import JSEditor from "./components/JSEditor";
import "./styles/App.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "./store";

import { AppState } from "./controller/AppState";
import { ActionType } from "./controller/Action";
import sm from "./controller";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  appState: st.controller.state
} );
const connector = connect( mapStateToProps );
type AppProps = ConnectedProps<typeof connector>;

// App component
class App extends React.Component<AppProps> {
  componentDidMount () {
    sm.move( { type: ActionType.SET_SPEC } );
  }
  renderInit () {
    return (
      <div>Loading ECMAScript 2021...</div>
    );
  }
  renderSuccess () {
    return (
      <Grid container className="app-container">
        <Grid item xs={ 12 }>
          <Toolbar />
        </Grid>
        <Grid item xs={ 12 }>
          <Grid container spacing={ 2 }>
            <Grid container item xs={ 9 } spacing={ 2 } style={ { padding: "0px 24px" } }>
              <Grid item xs={ 6 }>
                <JSEditor />
              </Grid>
              <Grid item xs={ 6 }>
                <SpecViewer />
              </Grid>
            </Grid>
            <Grid item xs={ 3 }>
              <StateViewer />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    );
  }
  render () {
    const { appState } = this.props;
    switch ( appState ) {
      case AppState.INIT:
        return this.renderInit();
      default:
        return this.renderSuccess();
    }
  }
}

export default connector( App );
