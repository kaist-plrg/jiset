import React from "react";
import Editor from "react-simple-code-editor";
import { highlight, languages } from "prismjs";
import "prismjs/components/prism-javascript";
import "prismjs/themes/prism.css";
import "../styles/JSEditor.css";
import { Typography, Paper } from "@material-ui/core";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

import { ActionType } from "../controller/Action";
import sm from "../controller";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  code: st.js.code
} );
const connector = connect( mapStateToProps );
type JSEditorProps = ConnectedProps<typeof connector>;

class JSEditor extends React.Component<JSEditorProps> {
  onCodeChange ( code: string ) {
    sm.move( { type: ActionType.EDIT_JS, code } );
  }

  highlightWithLine ( code: string ): string {
    return highlight( code, languages.js, "js" )
      .split( "\n" )
      .map( ( l, idx ) => `<span class="editor-line">${ idx + 1 } |</span>${ l }` )
      .join( "\n" );
  }

  render () {
    const { code } = this.props;
    return (
      <Paper className="editor-container" variant="outlined">
        <Typography variant="h6">JavaScript</Typography>
        <div className="editor-wrapper">
          <Editor
            value={ code }
            onValueChange={ ( code ) => this.onCodeChange( code ) }
            highlight={ ( code ) => this.highlightWithLine( code ) }
            padding={ 10 }
            style={ {
              fontFamily: '"Fira code", "Fira Mono", monospace',
              fontSize: 12,
            } }
            textareaId="editor-textarea"
            className="editor"
          />
        </div>
      </Paper>
    );
  }
}

export default connector( JSEditor );
