import React from "react";
import { v4 as uuid } from "uuid";
import { Typography } from "@material-ui/core";
import { Algo, getHeaderStr } from "../object/Algo";
import { AlgoStepNode, flattenList, Emitter } from "../util/ecmarkdown";
import { parseAlgorithm } from "ecmarkdown";
import type { AlgorithmNode } from "ecmarkdown";
import "../styles/AlgoViewer.css";

type AlgoStepProps = {
  node: AlgoStepNode;
  highlight: boolean;
};

class AlgoStep extends React.Component<AlgoStepProps> {
  getClassName (): string {
    let className = "algo-step";
    const { highlight } = this.props;
    if ( highlight ) className += " highlight";
    return className;
  }

  render () {
    let { node } = this.props;
    const { name, contents, indent, step } = node;

    const numberOrBullet = name.startsWith( "ordered" ) ? `${ step }. ` : '• ';
    const marginLeft = `${ indent * 16 }px`
    const className = this.getClassName();

    return (
      <div className={ className } style={ { marginLeft, color: 'black' } }>
        { numberOrBullet }
        { Emitter.emit(contents) }
      </div>
    );
  }
}

type AlgoViewerProps = {
  data: Algo | undefined;
  currentStep: number;
};
class AlgoViewer extends React.Component<AlgoViewerProps> {
  // TODO
  renderFail () {
    return <Typography variant="subtitle1">TODO...</Typography>;
  }

  flattenAlgo ( algo: AlgorithmNode ) {
    const { contents: ol } = algo;
    return flattenList( ol );
  }

  parseAlgo (algo: Algo) {
    const code = algo.code.join("\n");
    try {
      return this.flattenAlgo(parseAlgorithm(code));
    } catch (e) {
      return undefined;
    }
  }

  render () {
    const { data, currentStep } = this.props;
    if ( data === undefined ) return this.renderFail();
    // get header string
    const headerStr = getHeaderStr( data );

    const algo = this.parseAlgo(data);

    if (algo === undefined) return this.renderFail();

    // render
    return (
      <div className="algo-container">
        <Typography variant="subtitle1"><b>{ headerStr }</b></Typography>
        { algo.map( ( node, index ) => (
          <AlgoStep
            key={ uuid() }
            node={ node }
            highlight={ index === currentStep }
          />
        ) ) }
      </div>
    );
  }
}

export default AlgoViewer;
