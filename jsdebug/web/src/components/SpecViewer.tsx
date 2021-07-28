import React from "react";
import { Typography, Paper } from "@material-ui/core";
import AlgoViewer from "./AlgoViewer";
import { Algo } from "../object/Algo";

class SpecViewer extends React.Component {
  render () {
    // TODO replace mockup data
    const algo =
      {
        "head": {
          "params": [
            { "name": "input", "kind": "Normal" },
            { "name": "preferredType", "kind": "Optional" }
          ],
          "name": "ToPrimtive",
        },
        "id": "sec-toprimitive",
        "rawBody": "{\n  1:if (= (typeof input) Object) 1:{\n    2:(0) app __x0__ = (GetMethod input SYMBOL_toPrimitive)\n    2:let exoticToPrim = [? __x0__]\n    3:if (! (= exoticToPrim undefined)) 3:{\n      4:if (= preferredType absent) 4:let hint = \"default\" else 4:if (= preferredType CONST_string) 5:let hint = \"string\" else 6:{\n        7:assert (= preferredType CONST_number)\n        8:let hint = \"number\"\n      }\n      9:(1) app __x1__ = (Call exoticToPrim input (0) (new [hint]))\n      9:let result = [? __x1__]\n      10:if (! (= (typeof result) Object)) 10:return result else 10:{}\n      11:(1) throw TypeError\n    } else 3:{}\n    12:if (= preferredType absent) 12:let preferredType = CONST_number else 12:{}\n    13:(2) app __x2__ = (OrdinaryToPrimitive input preferredType)\n    13:return [? __x2__]\n  } else 1:{}\n  14:return input\n}",
        "code": [
          "1. Assert: _input_ is an ECMAScript language value.",
          "1. If Type(_input_) is Object, then",
          "  1. Let _exoticToPrim_ be ? GetMethod(_input_, @@toPrimitive).",
          "  1. If _exoticToPrim_ is not *undefined*, then",
          "    1. If _preferredType_ is not present, let _hint_ be *\"default\"*.",
          "    1. Else if _preferredType_ is ~string~, let _hint_ be *\"string\"*.",
          "    1. Else,",
          "      1. Assert: _preferredType_ is ~number~.",
          "      1. Let _hint_ be *\"number\"*.",
          "    1. Let _result_ be ? Call(_exoticToPrim_, _input_, « _hint_ »).",
          "    1. If Type(_result_) is not Object, return _result_.",
          "    1. Throw a *TypeError* exception.",
          "  1. If _preferredType_ is not present, let _preferredType_ be ~number~.",
          "  1. Return ? OrdinaryToPrimitive(_input_, _preferredType_).",
          "1. Return _input_."
        ],
      } as Algo;

    return (
      <Paper className="spec-viewer-container" variant="outlined">
        <Typography variant="h6">ECMAScript Specification</Typography>
        <AlgoViewer data={ algo } currentStep={ 3 } />
      </Paper>

    )

      ;
  }
}

export default SpecViewer;
