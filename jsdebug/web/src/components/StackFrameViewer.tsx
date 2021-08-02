import React from "react";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@material-ui/core";
import "../styles/StackFrameViewer.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { ActionType } from "../controller/Action";
import sm from "../controller";

type StackFrameItemProps = {
  // data: { name: string; step: number; focus: boolean; };
  data: string;
  idx: number;
}
class StackFrameItem extends React.Component<StackFrameItemProps> {
  render () {
    const { data, idx } = this.props;
    // const { name, step, focus } = data;

    return (
      <TableRow>
        <TableCell>{ idx }</TableCell>
        <TableCell>{ data }</TableCell>
      </TableRow>
    );

  }
}

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  stackFrames: st.ir.stackFrames
} );
const connector = connect( mapStateToProps );
type StackFrameViewerProps = ConnectedProps<typeof connector>;

class StackFrameViewer extends React.Component<StackFrameViewerProps> {
  render () {
    const { stackFrames } = this.props;

    return ( <div className="stackframe-container">
      <TableContainer component={ Paper } className="stackframe-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableCell>Frame #</TableCell>
            <TableCell>name</TableCell>
          </TableHead>
          <TableBody>
            { stackFrames.map( ( context, idx ) => (
              <StackFrameItem data={ context } idx={ idx } />
            ) ) }
          </TableBody>
        </Table>

      </TableContainer>

    </div> );
  }
}

export default connector(StackFrameViewer);
