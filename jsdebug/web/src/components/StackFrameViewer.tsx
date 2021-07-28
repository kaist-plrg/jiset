import React from "react";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@material-ui/core";
import "../styles/StackFrameViewer.css";

type StackFrameItemProps = {
  data: { name: string; step: number; focus: boolean; };
  idx: number;
}
class StackFrameItem extends React.Component<StackFrameItemProps> {
  render () {
    const { data, idx } = this.props;
    const { name, step, focus } = data;

    return (
      <TableRow>
        <TableCell>{ idx }</TableCell>
        <TableCell>step { step } @ { name }</TableCell>
      </TableRow>
    );

  }
}

class StackFrameViewer extends React.Component {
  render () {
    // TODO replace mockup data
    const contexts = [
      { name: "ToPrimtive", step: 0, focus: false },
      { name: "ToPrimtive", step: 1, focus: false },
      { name: "ToPrimtive", step: 2, focus: false },
      { name: "ToPrimtive", step: 3, focus: false },
      { name: "ToPrimtive", step: 4, focus: true },
      { name: "ToPrimtive", step: 5, focus: false },
      { name: "ToPrimtive", step: 6, focus: false },
      { name: "ToPrimtive", step: 7, focus: false },
      { name: "ToPrimtive", step: 8, focus: false },
      { name: "ToPrimtive", step: 9, focus: false },
      { name: "ToPrimtive", step: 10, focus: false },
    ];

    return ( <div className="stackframe-container">
      <TableContainer component={ Paper } className="stackframe-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableCell>Frame #</TableCell>
            <TableCell>name</TableCell>
          </TableHead>
          <TableBody>
            { contexts.map( ( context, idx ) => (
              <StackFrameItem data={ context } idx={ idx } />
            ) ) }
          </TableBody>
        </Table>

      </TableContainer>

    </div> );
  }
}

export default StackFrameViewer;
