import { combineReducers } from "redux";
import appState from "./AppState";
import spec from "./Spec";
import js from "./JS";
import webDebugger from "./Debugger";
import ir from "./IR";

export default combineReducers({
  appState,
  spec,
  js,
  webDebugger,
  ir,
});
