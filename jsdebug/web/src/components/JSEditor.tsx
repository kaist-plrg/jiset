import React from "react";
import Editor from "react-simple-code-editor";
import { highlight, languages } from "prismjs";
import "prismjs/components/prism-javascript";
import "prismjs/themes/prism.css";
import "../styles/JSEditor.css";
import { Typography, Paper } from "@material-ui/core";

type JSEditorProps = {};
type JSEditorState = { code: string };

class JSEditor extends React.Component<JSEditorProps, JSEditorState> {
  constructor ( props: JSEditorProps ) {
    super( props );
    this.state = {
      code: "var x = 1 + 2;",
    };
  }

  onCodeChange ( code: string ) {
    this.setState( { ...this.state, code } );
  }

  highlightWithLine ( code: string ): string {
    return highlight( code, languages.js, "js" )
      .split( "\n" )
      .map( ( l, idx ) => `<span class="editor-line">${ idx + 1 } |</span>${ l }` )
      .join( "\n" );
  }

  render () {
    const { code } = this.state;
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

export default JSEditor;
