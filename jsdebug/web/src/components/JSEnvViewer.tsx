import React from "react";
import EnvViewer from "./EnvViewer";

import { connect, ConnectedProps } from "react-redux";
import { ReduxState } from "../store";

// connect redux store
const mapStateToProps = ( st: ReduxState ) => ( {
  heap: st.ir.heap,
} );
const connector = connect( mapStateToProps );
type JSEnvViewerProps = ConnectedProps<typeof connector>;

class JSEnvViewer extends React.Component<JSEnvViewerProps> {
  getContent( heapValue: string, key: string ) {
    let contentList = heapValue.slice(heapValue.indexOf('{'), heapValue.indexOf('}')).split('\n');
    let forContent = contentList.filter( str => str.includes("\""+key+"\""))[0];
    return forContent.slice(key.length + 8);
  }

  render () {
    const { heap } = this.props;
    let env: [ string, string ][] = [];
    let execStk = heap[ "#EXECUTION_STACK" ]
    if ( execStk !== undefined ) {
      let execStkList = execStk.slice(1, -1).split(',');
      if ( execStkList.length === 2 ) {
        // get variable names
        let content = heap[ execStkList[1].slice(1) ];
        let lexicalEnvAddr = this.getContent(content, "LexicalEnvironment").slice(2, -1);
        let lexicalEnv = heap[ lexicalEnvAddr ];
        let varNamesAddr = this.getContent(lexicalEnv, "VarNames");
        let varNames = heap[ varNamesAddr ];
        let vars = varNames.slice(1, -1).split(', ');
        vars = vars.map((x) => x.slice(1, -1));

        // get values of variables
        let globalObjAddr = this.getContent(heap[ "#REALM" ], "GlobalObject");
        let globalObj = heap[ globalObjAddr ];
        let submapAddr = this.getContent(globalObj, "SubMap");
        let submap = heap[ submapAddr ];

        // push to env
        vars.forEach( (x) => {
          let valueAddr = this.getContent(submap, x);
          let value = heap[ valueAddr ];
          let getValue = this.getContent(value, "Value");
          env.push([x, getValue]);
        });
      }
    }
    return (
      <EnvViewer env={ env } />
    );
  }
}

export default connector( JSEnvViewer );
