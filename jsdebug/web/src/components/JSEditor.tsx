import React from "react";
import Editor from "react-simple-code-editor";
import { v4 as uuid } from "uuid";
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
  js: st.js,

} );
const connector = connect( mapStateToProps );
type JSEditorProps = ConnectedProps<typeof connector>;

class JSEditor extends React.Component<JSEditorProps> {
  onCodeChange ( code: string ) {
    sm.move( { type: ActionType.EDIT_JS, code } );
  }
  private genMarker (): [ string, string ] {
    let genUid = () => ` __${ uuid().replaceAll( "-", "_" ) }__ `
    return [ ` ${ genUid() } `, ` ${ genUid() } ` ];
  }
  highlightWithLine ( code: string, line: number = 0, start: number = -1, end: number = -1, breakpoints: { line: number, enable: boolean }[]): string {
    let highlighted: string;
    // use highlighting when start, end index is given
    if ( start >= 0 && end >= 0 ) {
      const [ startMarker, endMarker ] = this.genMarker();
      const marked = code.slice( 0, start ) + startMarker + code.slice( start, end ) + endMarker + code.slice( end, code.length );
      highlighted = highlight( marked, languages.js, "js" )
        .replace( startMarker, "<mark>" )
        .replace( endMarker, "</mark>" ); 
    } else highlighted = highlight( code, languages.js, "js" );
    // decorate with line info
    return highlighted
      .split( "\n" )
      .map( ( l, idx ) => {
        let codeStr = `${ l }`
        if ( line > 0 && line === (idx + 1) ) { codeStr = `<span class="editor-line-now">${ l }</span>`}
        if ( breakpoints.some( bp => ((bp.line === (idx + 1)) && bp.enable) ) ) {
          return `<span class="editor-line-break">${ idx + 1 } |</span>` + codeStr
        } else { return `<span class="editor-line">${ idx + 1 } |</span>` + codeStr }
      })
      .join( "\n" );
  }

  render () {
    const { code, line, start, end, breakpoints } = this.props.js;
    return (
      <Paper className="editor-container" variant="outlined">
        <Typography variant="h6">JavaScript</Typography>
        <div className="editor-wrapper">
          <Editor
            value={ code }
            onValueChange={ ( code ) => this.onCodeChange( code ) }
            highlight={ ( code ) => this.highlightWithLine( code, line, start, end, breakpoints ) }
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
