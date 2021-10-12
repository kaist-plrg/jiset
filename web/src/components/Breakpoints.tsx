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

import { Breakpoint } from "../object/Breakpoint";
import { connect, ConnectedProps } from "react-redux";
import { ReduxState, Dispatch } from "../store";
import { addBreak, rmBreak, toggleBreak } from "../store/reducers/Debugger";

type BreakpointItemProp = {
  data: Breakpoint;
  idx: number;
  onRemoveClick: (opt: string) => void;
  onToggleClick: (opt: string) => void;
};

class BreakpointItem extends React.Component<BreakpointItemProp> {
  onToggleClick () {
    const { idx, onToggleClick } = this.props;
    onToggleClick(idx.toString());
  }
  onRemoveClick () {
    const { idx, onRemoveClick } = this.props;
    onRemoveClick(idx.toString());
  }
  render () {
    const { data } = this.props;
    const { name, enabled } = data;
    return (
      <TableRow>
        <TableCell style={ { width: "50%", overflow: "hidden" } }>
          <Tooltip title={ name }>
            <span>{ name }</span>
          </Tooltip>
        </TableCell>
        <TableCell style={ { width: "15%" } }>
          <Switch checked={ enabled } onChange={ () => this.onToggleClick() } />
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
const mapDispatchToProps = (dispatch : Dispatch) => ( {
  addBreak: (bpName: string) => dispatch(addBreak(bpName)),
  rmBreak: (opt: string) => dispatch(rmBreak(opt)),
  toggleBreak: (opt: string) => dispatch(toggleBreak(opt)),
});
const connector = connect( mapStateToProps, mapDispatchToProps );
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
      .some( ( {name} ) => name === bpName );
    const valid = this.props.algoNames.some( ( name ) => name === bpName );
    if ( valid && !duplicated ) this.props.addBreak(bpName);
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
                <BreakpointItem 
                  key={ uuid() } 
                  data={ bp } 
                  idx={ idx } 
                  onRemoveClick={(opt: string) => this.props.rmBreak(opt)} 
                  onToggleClick= {(opt: string) => this.props.toggleBreak(opt)}
                />
              ) ) }
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    );
  }
}

export default connector( Breakpoints );
