import React from "react";
import { v4 as uuid } from "uuid";
import { TextField, Button, Switch, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@material-ui/core";
import "../styles/Breakpoints.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { ActionType } from "../controller/Action";
import sm from "../controller";

type Breakpoint = {
  name: string;
  enable: boolean;
};

type BreakpointItemProp = {
  data: Breakpoint;
  idx: number;
};
type BreakpointItemState = {
  checked: boolean;
};

class BreakpointItem extends React.Component<BreakpointItemProp, BreakpointItemState> {
  constructor ( props: BreakpointItemProp ) {
    super( props );
    this.state = { checked: this.props.data.enable };
  }

  onEnableChange ( idx: number ) { 
    this.setState( { ...this.state, checked: !(this.state.checked) } );
    sm.move( { type: ActionType.ABLE_BREAK, idx } );
  };
  render () {
    const { data, idx } = this.props;
    const { name, enable } = data;
    const { checked } = this.state;
    return (
      <TableRow>
        <TableCell>{ idx }</TableCell>
        <TableCell>{ name }</TableCell>
        <TableCell>
          <Switch checked={ checked } onChange={() => this.onEnableChange(idx)} />
        </TableCell>
      </TableRow>
    );
  }
}

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  breakpoints: st.webDebugger.breakpoints,
} );
const connector = connect( mapStateToProps );
type BreakpointsProps = ConnectedProps<typeof connector>;

// TODO add util buttons
// delete
// delete all
// sort
// disable
// disable all
class Breakpoints extends React.Component<BreakpointsProps> {
  inputProps = { addName: "", rmName: "" };

  onAddChange ( name: string ) { this.inputProps.addName = name; };
  onAddClick () {
    sm.move( { type: ActionType.ADD_BREAK, bpName: this.inputProps.addName } );
  };
  onRemoveChange ( name: string ) { this.inputProps.rmName = name; };
  onRemoveClick () {
    sm.move( { type: ActionType.RM_BREAK, bpName: this.inputProps.rmName } );
  };

  render () {
    const { breakpoints } = this.props
    return ( <div className="breakpoints-container">
      <TextField label="Add a Breakpoint" variant="outlined" size="small"
        onChange={ ( name ) => this.onAddChange( name.target.value )}
      />
      <Button variant="contained" onClick={ () => this.onAddClick() }>Add</Button>
      <TextField label="Remove a Breakpoint" variant="outlined" size="small"
        onChange={ ( name ) => this.onRemoveChange( name.target.value )}
      />
      <Button variant="contained" onClick={ () => this.onRemoveClick() }>Remove</Button>

      <TableContainer component={ Paper } className="breakpoints-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              <TableCell>Breakpoint #</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Enable</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            { breakpoints.map( ( bp ) => (
              <BreakpointItem key={ uuid() } data={ bp } idx={ breakpoints.indexOf(bp) } />
            ) ) }
          </TableBody>
        </Table>

      </TableContainer>

    </div> );
  }
}

export default connector( Breakpoints );
