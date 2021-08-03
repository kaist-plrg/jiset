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
import { v4 as uuid } from "uuid";
import "../styles/StackFrameViewer.css";

import { connect, ConnectedProps } from "react-redux";
import { StackFrameData } from "../store/reducers/IR";
import { ReduxState } from "../store";

import { ActionType } from "../controller/Action";
import sm from "../controller";

type StackFrameItemProps = {
  data: StackFrameData;
  highlight: boolean;
  idx: number;
  onItemClick: ( idx: number ) => void;
};
class StackFrameItem extends React.Component<StackFrameItemProps> {
  getClassName (): string {
    let className = "stackframe-item";
    const { highlight } = this.props;
    if ( highlight ) className += " highlight";
    return className;
  }
  render () {
    const { data, idx, onItemClick } = this.props;
    const [ name, step ] = data;
    const content = step === -1 ? name : `${ step } @ ${ name }`;

    return (
      <TableRow
        className={ this.getClassName() }
        onClick={ () => onItemClick( idx ) }
      >
        <TableCell>{ idx }</TableCell>
        <TableCell>{ content }</TableCell>
      </TableRow>
    );
  }
}

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  stackFrame: st.ir.stackFrame,
} );
const connector = connect( mapStateToProps );
type StackFrameViewerProps = ConnectedProps<typeof connector>;

class StackFrameViewer extends React.Component<StackFrameViewerProps> {
  onItemClick ( idx: number ) {
    sm.move( { type: ActionType.SHOW_ALGO, idx } );
  }
  render () {
    const { stackFrame } = this.props;
    const { data, idx } = stackFrame;

    return (
      <div className="stackframe-container">
        <TableContainer
          component={ Paper }
          className="stackframe-table-container"
        >
          <Table stickyHeader size="small">
            <TableHead>
              <TableRow>
                <TableCell>Frame #</TableCell>
                <TableCell>name</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              { data.map( ( ctxtInfo, ctxtIdx ) => (
                <StackFrameItem
                  key={ uuid() }
                  data={ ctxtInfo }
                  idx={ ctxtIdx }
                  highlight={ idx === ctxtIdx }
                  onItemClick={ ( idx: number ) => this.onItemClick( idx ) }
                />
              ) ) }
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    );
  }
}

export default connector( StackFrameViewer );
