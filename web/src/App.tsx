import React from "react";
import { Grid } from "@material-ui/core";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import SpecViewer from "./components/SpecViewer";
import Toolbar from "./components/Toolbar";
import StateViewer from "./components/StateViewer";
import JSEditor from "./components/JSEditor";
import "./styles/App.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState, Dispatch } from "./store";

import { AppState } from "./store/reducers/AppState";
import { loadSpecRequest } from "./store/reducers/Spec";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  appState: st.appState.state,
} );
const mapDispatchToProps = (dispatch : Dispatch) => ( {
  loadSpecRequest: () => dispatch( loadSpecRequest() )
});
const connector = connect( mapStateToProps, mapDispatchToProps );
type AppProps = ConnectedProps<typeof connector>;

// App component
class App extends React.Component<AppProps> {
  componentDidMount () {
    this.props.loadSpecRequest();
  }
  renderInit () {
    return <div>Loading ECMAScript 2021...</div>;
  }
  renderSuccess () {
    return (
      <div>
        <ToastContainer
          autoClose={ 3000 }
          hideProgressBar={ true }
        />
        <Grid container className="app-container">
          <Grid item xs={ 12 }>
            <Toolbar />
          </Grid>
          <Grid item xs={ 12 }>
            <Grid container spacing={ 2 } style={ { width: "100%" } }>
              <Grid
                container
                item
                xs={ 9 }
                spacing={ 2 }
                style={ { padding: "0px 24px" } }
              >
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
      </div>
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
