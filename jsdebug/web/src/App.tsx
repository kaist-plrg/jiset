import React from "react";
import "./App.css";
import SpecViewer from "./components/SpecViewer";
import Stat from "./components/Stat";
import Toolbar from "./components/Toolbar";
import StateViewer from "./components/StateViewer";
import Breakpoints from "./components/Breakpoints";
import JSEditor from "./components/JSEditor";
import { Grid } from "@material-ui/core";

class App extends React.Component {
  render () {
    return (
      <Grid container className="app-container">
        <Grid item xs={ 12 }>
          <Toolbar />
        </Grid>
        <Grid item xs={ 12 }>
          <Grid container spacing={ 2 }>
            <Grid container item xs={ 9 } spacing={ 2 } style={ { padding: "0px 8px" } }>
              <Grid item xs={ 6 }>
                <JSEditor />
              </Grid>
              <Grid item xs={ 6 }>
                <SpecViewer />
              </Grid>
            </Grid>
            <Grid item xs={ 3 }>
              <StateViewer />
              <Breakpoints />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    );
  }
}

export default App;
