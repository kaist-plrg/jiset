import React from "react";
import { v4 as uuid } from "uuid";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@material-ui/core";
import "../styles/EnvViewer.css";

type EnvViewerProps = {
  // pair of variable name and value
  env: [ string, string ][]
}
class EnvViewer extends React.Component<EnvViewerProps> {
  render () {
    const { env } = this.props;

    return ( <div className="env-viewer-container">
      <TableContainer component={ Paper } className="env-viewer-table-container">
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              <TableCell>name</TableCell>
              <TableCell>value</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            { env.map( ( [ name, value ] ) => (
              <TableRow key={ uuid() }>
                <TableCell>{ name }</TableCell>
                <TableCell>{ value }</TableCell>
              </TableRow>
            ) ) }
          </TableBody>
        </Table>

      </TableContainer>

    </div> );
  }
}

export default EnvViewer;
