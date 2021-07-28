import React from "react";
import { AppBar, Tabs, Tab } from "@material-ui/core";
import { DataGrid, GridRowsProp, GridColDef } from "@material-ui/data-grid";
import "../styles/StateWatcher.css";

// TabPanel
type StateTabPanelProps = {
  index: number;
  value: number;
  rows: GridRowsProp;
};

// StateWatcher DataGrid
const _gridCols: GridColDef[] = [
  { field: "name", headerName: "ID", editable: true },
  { field: "value", headerName: "Value", width: 140 },
];
class StateTabPanel extends React.Component<StateTabPanelProps> {
  renderDataGrid () {
    const { rows } = this.props;
    return (
      <div style={ { height: "100%" } }>
        <DataGrid rows={ rows } columns={ _gridCols } />
      </div>
    );
  }
  render () {
    const { index, value } = this.props;
    return <>{ index === value && this.renderDataGrid() }</>;
  }
}

// WatchViewer
type StateWatcherProps = {};
type StateWatcherState = { value: number };

class StateWatcher extends React.Component<StateWatcherProps, StateWatcherState> {
  constructor ( props: {} ) {
    super( props );
    this.state = { value: 0 };
  }
  onTabsChange ( value: number ) {
    this.setState( { ...this.state, value } );
  }
  render () {

    const { value } = this.state;

    // TODO replace mockup data
    const jsRows: GridRowsProp = [ { id: 1, name: "x", value: "1" } ];
    const esRows: GridRowsProp = [
      { id: 1, name: "x", value: "1" },
      { id: 3, name: "z", value: "3" },
    ];
    const irRows: GridRowsProp = [
      { id: 1, name: "x", value: "1" },
      { id: 2, name: "__y__", value: "2" },
      { id: 3, name: "z", value: "3" },
    ];

    return (
      <div className="watcher-container">
        <AppBar position="static" >
          <Tabs
            className="watcher-tabs"
            value={ value }
            onChange={ ( _, newValue ) => this.onTabsChange( newValue ) }
            aria-label="state viewer tabs"
            variant="fullWidth"
          >
            <Tab className="watcher-tab-item" label="JavaScript" />
            <Tab className="watcher-tab-item" label="ECMAScript" />
            <Tab className="watcher-tab-item" label="IR_es" />
          </Tabs>
        </AppBar>
        <div className="watcher-panel-container">
          <StateTabPanel index={ 0 } value={ value } rows={ jsRows } />
          <StateTabPanel index={ 1 } value={ value } rows={ esRows } />
          <StateTabPanel index={ 2 } value={ value } rows={ irRows } />
        </div>
      </div>
    );
  }
}

export default StateWatcher;
