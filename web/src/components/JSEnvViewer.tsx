import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@material-ui/core";
import "../styles/JSEnvViewer.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState, Dispatch } from "../store";

type JSEnvStackProps = {
  idx: number;
  highlight: boolean;
  onItemClick: ( idx: number ) => void;
};
class JSEnvStack extends React.Component<JSEnvStackProps> {
  getClassName (): string {
    let className = "JSenv-stack";
    const { highlight } = this.props;
    if ( highlight ) className += " highlight";
    return className;
  }
  render () {
    const { idx, onItemClick } = this.props;

    return (
      <TableRow
        className={ this.getClassName() }
        onClick={ () => onItemClick( idx ) }
      >
        <TableCell>{ idx }</TableCell>
      </TableRow>
    );
  }
}

type JSEnvItemProps = {
  show: boolean;
  name: string;
  value: string;
};
class JSEnvItem extends React.Component<JSEnvItemProps> {
  render () {
    const { show, name, value } = this.props;

    if ( show ) {
      return (
        <TableRow className="JSenv-item">
          <TableCell>{ name }</TableCell>
          <TableCell>{ value }</TableCell>
        </TableRow>
      );
    }
    return ( null );
  }
}

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  env: st.ir.env,
} );
const mapDispatchToProps = (dispatch : Dispatch) => ( {
  dispatch
});
const connector = connect( mapStateToProps, mapDispatchToProps );
type JSEnvViewerProps = ConnectedProps<typeof connector>;

class JSEnvViewer extends React.Component<JSEnvViewerProps> {
  onItemClick ( idx: number ) {
  }

  render () {
    const { env } = this.props;
    const { data, idx } = env;
    return ( <div className="JSenv-viewer-container">
      <TableContainer className="JSenv-viewer-stack-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              <TableCell>Environment #</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            { Array.from( data.keys() ).map( ( envIdx ) => (
              <JSEnvStack
                idx={ envIdx }
                highlight={ idx === envIdx }
                onItemClick={ ( idx: number ) => this.onItemClick( idx ) }
              />
            ) ) }
          </TableBody>
        </Table>
      </TableContainer>
      <TableContainer component={ Paper } className="JSenv-viewer-item-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Value</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            { data.map( ( list, i ) => (
              list.map( ( [ name, value ] ) => (
                <JSEnvItem show={ i === idx } name={ name } value={ value } />
              ) )
            ) ) }
          </TableBody>
        </Table>
      </TableContainer>
    </div> );
  }
}

export default connector( JSEnvViewer );
