import React from "react";
import { toast } from "react-toastify";
import { v4 as uuid } from "uuid";
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
import { ReduxState, Dispatch } from "../store";

type JSBreakpoint = {
  line: number;
  enable: boolean;
};

type JSBreakpointItemProp = {
  data: JSBreakpoint;
  idx: number;
};

class JSBreakpointItem extends React.Component<JSBreakpointItemProp> {
  onEnableChange () {
    const { idx } = this.props;
    // sm.move( { type: ActionType.TOGGLE_BREAK_JS, opt: idx.toString() } );
  }
  onRemoveClick () {
    const { idx } = this.props;
    // sm.move( { type: ActionType.RM_BREAK_JS, opt: idx.toString() } );
  }
  render () {
    const { data } = this.props;
    const { line, enable } = data;
    return (
      <TableRow>
        <TableCell style={ { width: "30%" } }>
          <Tooltip title={ line }>
            <span>{ line }</span>
          </Tooltip>
        </TableCell>
        <TableCell style={ { width: "30%" } }>
          <Switch checked={ enable } onChange={ () => this.onEnableChange() } />
        </TableCell>
        <TableCell style={ { width: "30%" } }>
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
  breakpoints: st.js.breakpoints,
  code: st.js.code,
} );
const connector = connect( mapStateToProps );
type JSBreakpointsProps = ConnectedProps<typeof connector>;
type JSBreakpointsState = { line: string };

// TODO add util buttons
// delete all
// disable all
// sort
class JSBreakpoints extends React.Component<JSBreakpointsProps, JSBreakpointsState> {
  constructor ( props: JSBreakpointsProps ) {
    super( props );
    this.state = { line: "" };
  }

  onAddChange ( line: string ) {
    this.setState( { ...this.state, line } );
  }
  onAddClick () {
    const line = Number( this.state.line );
    const duplicated = this.props.breakpoints
      .map( ( _ ) => _.line )
      .some( ( _ ) => _ === line );
    const valid = ( line <= this.props.code.split( '\n' ).length ) && ( line > 0 );
    if ( valid && !duplicated ) {}
      // sm.move( { type: ActionType.ADD_BREAK_JS, line: Number( this.state.line ) } );
    else if ( duplicated ) toast.warning( `Breakpoint already set: Line ${ line }` );
    else toast.warning( `Out of Range: Line ${ line }` );
  }

  render () {
    const { breakpoints } = this.props;
    const { line } = this.state;

    return (
      <div className="breakpoints-container">
        <TextField
          label="Line Number"
          variant="outlined"
          size="small"
          fullWidth
          value={ line }
          margin="normal"
          onChange={ ( event ) => this.onAddChange( event.target.value ) }
          InputProps={ {
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
        />
        <TableContainer
          component={ Paper }
          className="breakpoints-table-container"
        >
          <Table stickyHeader size="small">
            <TableHead>
              <TableRow>
                <TableCell style={ { width: "30%" } }>Line#</TableCell>
                <TableCell style={ { width: "30%" } }>Enable</TableCell>
                <TableCell style={ { width: "30%" } }>Remove</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              { breakpoints.map( ( bp, idx ) => (
                <JSBreakpointItem key={ uuid() } data={ bp } idx={ idx } />
              ) ) }
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    );
  }
}

export default connector( JSBreakpoints );
