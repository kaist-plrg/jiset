import React from "react";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@material-ui/core";
import "../styles/Breakpoints.css";

type Breakpoint = {
  enable: boolean;
  name: string;
};

type BreakpointItemProp = {
  data: Breakpoint;
  idx: number;
};
class BreakpointItem extends React.Component<BreakpointItemProp> {
  render () {
    const { data, idx } = this.props;
    const { name, enable } = data;
    return (
      <TableRow>
        <TableCell>{ idx }</TableCell>
        <TableCell>{ name }</TableCell>
      </TableRow>
    );
  }
}

// TODO add util buttons
// delete
// delete all
// sort
// disable
// disable all
class Breakpoints extends React.Component {
  render () {
    // TODO replace mockup data
    const breakpoints: Breakpoint[] = [
      { enable: true, name: "RunJobs" },
      { enable: false, name: "ToPrimtive" },
      { enable: true, name: "ToObject" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
      { enable: true, name: "ToBoolean" },
    ];

    return ( <div className="breakpoints-container">
      <TableContainer component={ Paper } className="breakpoints-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              <TableCell>Breakpoint #</TableCell>
              <TableCell>name</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            { breakpoints.map( ( bp, idx ) => (
              <BreakpointItem key={ `breakpoints-${ idx }` } data={ bp } idx={ idx } />
            ) ) }
          </TableBody>
        </Table>

      </TableContainer>

    </div> );
  }
}

export default Breakpoints;
