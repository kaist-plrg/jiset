import React from "react";
import { Button, ButtonGroup } from "@material-ui/core";
import "../styles/Toolbar.css";

class Toolbar extends React.Component {
  render () {
    return (
      <div className="toolbar-container">
        <ButtonGroup variant="text" color="primary">
          <Button>Run</Button>
          <Button>Terminate</Button>
          <Button>Step</Button>
          <Button>Step-Over</Button>
          <Button>Step-Out</Button>
          <Button>Continue</Button>
        </ButtonGroup>
      </div>
    );
  }
}

export default Toolbar;
