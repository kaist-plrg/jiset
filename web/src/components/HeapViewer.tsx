import React from "react";
import { TextField } from "@material-ui/core";
import { Autocomplete } from "@material-ui/lab";
import "../styles/HeapViewer.css";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  heap: st.ir.heap,
} );
const connector = connect( mapStateToProps );
type HeapViewerProps = ConnectedProps<typeof connector>;
type HeapViewerState = { searchAddr: string }

class HeapViewer extends React.Component<HeapViewerProps, HeapViewerState> {
  constructor ( props: HeapViewerProps ) {
    super( props );
    this.state = { searchAddr: "" };
  }
  onTextInput ( searchAddr: string ) {
    this.setState( { ...this.state, searchAddr } );
  }
  renderObj ( obj: string | undefined ) {
    return obj === undefined ? ( <span>NOT FOUND</span> ) : ( <pre>{ obj }</pre> );
  }
  render () {
    const { heap } = this.props;
    const { searchAddr } = this.state;
    const addrs = Object.keys( heap );
    const obj = heap[ searchAddr ];
    return (
      <div className="heap-viewer-container">
        <Autocomplete
          freeSolo
          disableClearable
          options={ addrs }
          onChange={ ( _, value ) => this.onTextInput( value ) }
          renderInput={ ( params ) => (
            <TextField
              { ...params }
              label="Heap Address"
              size="small"
              margin="normal"
              variant="outlined"
              onChange={ ( event ) => this.onTextInput( event.target.value ) }
              value={ searchAddr }
              InputProps={ { ...params.InputProps, type: "search" } }
            />
          ) }
        />
        <div className="heap-viewer-obj-wrapper">
          { this.renderObj( obj ) }
        </div>
      </div>
    );
  }
}

export default connector( HeapViewer );
