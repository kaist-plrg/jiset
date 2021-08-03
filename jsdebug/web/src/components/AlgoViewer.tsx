import React from "react";
import { v4 as uuid } from "uuid";
import { Typography } from "@material-ui/core";
import { Algo, getHeaderStr } from "../object/Algo";
import "../styles/AlgoViewer.css";

type AlgoStepProps = {
  content: string;
  step: number;
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
    let { content } = this.props;
    const className = this.getClassName();
    return (
      <Typography variant="body2" className={ className }>
        { content }
      </Typography>
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
  render () {
    const { data, currentStep } = this.props;
    if ( data === undefined ) return this.renderFail();
    // get header string
    const headerStr = getHeaderStr( data );
    // calculate left space
    let spaceCnt = 0;
    if ( data.code.length > 0 )
      spaceCnt = data.code[ 0 ].length - data.code[ 0 ].trimLeft().length;
    // render
    return (
      <div className="algo-container">
        <Typography variant="subtitle1"><b>{ headerStr }</b></Typography>
        { data.code.map( ( str, idx ) => (
          <AlgoStep
            key={ uuid() }
            content={ str.slice( spaceCnt ) }
            step={ idx }
            highlight={ idx === currentStep }
          />
        ) ) }
      </div>
    );
  }
}

export default AlgoViewer;
