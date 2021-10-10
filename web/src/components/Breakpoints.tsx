import React from "react";
import { toast } from "react-toastify";
import { v4 as uuid } from "uuid";
import { Autocomplete } from "@material-ui/lab";
import {
  Tooltip,
  IconButton,
  Icon,
  TextField,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@material-ui/core";
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

class BreakpointItem extends React.Component<BreakpointItemProp> {
  onEnableChange () {
    const { idx } = this.props;
    sm.move( { type: ActionType.TOGGLE_BREAK, opt: idx.toString() } );
  }
  onRemoveClick () {
    const { idx } = this.props;
    sm.move( { type: ActionType.RM_BREAK, opt: idx.toString() } );
  }
  render () {
    const { data } = this.props;
    const { name, enable } = data;
    return (
      <TableRow>
        <TableCell style={ { width: "50%", overflow: "hidden" } }>
          <Tooltip title={ name }>
            <span>{ name }</span>
          </Tooltip>
        </TableCell>
        <TableCell style={ { width: "15%" } }>
          <Switch checked={ enable } onChange={ () => this.onEnableChange() } />
        </TableCell>
        <TableCell style={ { width: "15%" } }>
          <IconButton component="span" onClick={ () => this.onRemoveClick() }>
            <Icon color="secondary">remove_circle</Icon>
          </IconButton>
        </TableCell>
      </TableRow>
    );
  }
}

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  breakpoints: st.webDebugger.breakpoints,
  algoNames: st.spec.algoNames,
} );
const connector = connect( mapStateToProps );
type BreakpointsProps = ConnectedProps<typeof connector>;
type BreakpointsState = { bpName: string };

// TODO add util buttons
// delete all
// disable all
// sort
// type Hack = { [ key: string ]: any };
class Breakpoints extends React.Component<BreakpointsProps, BreakpointsState> {
  constructor ( props: BreakpointsProps ) {
    super( props );
    this.state = { bpName: "" };
  }

  onAddChange ( bpName: string ) {
    this.setState( { ...this.state, bpName } );
  }
  onAddClick () {
    const bpName = this.state.bpName;
    const duplicated = this.props.breakpoints
      .map( ( _ ) => _.name )
      .some( ( _ ) => _ === bpName );
    const valid = this.props.algoNames.some( ( name ) => name === bpName );
    if ( valid && !duplicated )
      sm.move( { type: ActionType.ADD_BREAK, bpName: this.state.bpName } );
    else if ( duplicated ) toast.warning( `Breakpoint already set: ${ bpName }` );
    else toast.warning( `Wrong breakpoint name: ${ bpName }` );
  }

  render () {
    const { breakpoints, algoNames } = this.props;
    const { bpName } = this.state;

    return (
      <div className="breakpoints-container">
        <Autocomplete
          freeSolo
          disableClearable
          options={ algoNames }
          onChange={ ( _, value ) => this.onAddChange( value ) }
          renderInput={ ( params ) => (
            <TextField
              { ...params }
              label="Algorithm Name"
              variant="outlined"
              size="small"
              value={ bpName }
              margin="normal"
              InputProps={ {
                ...params.InputProps,
                type: "search",
                endAdornment: (
                  <IconButton
                    style={ { padding: 0 } }
                    onClick={ () => this.onAddClick() }
                  >
                    <Icon>add_circle</Icon>
                  </IconButton>
                ),
              } }
              onChange={ ( event ) => this.onAddChange( event.target.value ) }
            />
          ) }
        />
        <TableContainer
          component={ Paper }
          className="breakpoints-table-container"
        >
          <Table stickyHeader size="small">
            <TableHead>
              <TableRow>
                <TableCell style={ { width: "50%" } }>Name</TableCell>
                <TableCell style={ { width: "15%" } }>Enable</TableCell>
                <TableCell style={ { width: "15%" } }>Remove</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              { breakpoints.map( ( bp, idx ) => (
                <BreakpointItem key={ uuid() } data={ bp } idx={ idx } />
              ) ) }
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    );
  }
}

export default connector( Breakpoints );
