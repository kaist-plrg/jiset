import { combineReducers } from "redux";
import controller from "./Controller";
import spec from "./Spec";
import js from "./JS";
import webDebugger from "./Debugger";
import ir from "./IR";

export default combineReducers({
  controller,
  spec,
  js,
  webDebugger,
  ir,
});
