import { Store } from "../store";

// spec
import es2021 from "../assets/es2021.json";
import { loadSpec } from "../store/reducers/Spec";
import { Spec } from "../object/Spec";

// js
import { editJS } from "../store/reducers/JS";

// debugger
import {
  loadDebugger,
  runDebugger,
  pauseDebugger,
} from "../store/reducers/Debugger";

// ir
import { updateIrInfo } from "../store/reducers/IR";

// possible action types
export enum ActionType {
  SET_SPEC = "ActionType/SET_SPEC",
  EDIT_JS = "ActionType/EDIT_JS",
  START_DBG = "ActionType/START_DBG",
  STEP = "ActionType/STEP",
  STEP_OVER = "ActionType/STEP_OVER",
  STEP_OUT = "ActionType/STEP_OUT",
  CONTINUE = "ActionType/CONTINUE",
  TERMINATE = "ActionType/TERMINATE",
  STOP_DBG = "ActionType/STOP_DBG",
}

// action payload
export type ActionPayload =
  | { type: ActionType.SET_SPEC }
  | {
    type: ActionType.EDIT_JS;
    code: string;
  }
  | { type: ActionType.START_DBG }
  | { type: ActionType.STEP }
  | { type: ActionType.STEP_OVER }
  | { type: ActionType.STEP_OUT }
  | { type: ActionType.CONTINUE }
  | { type: ActionType.TERMINATE }
  | { type: ActionType.STOP_DBG };

// actions
export type Action = ( store: Store, ...args: any[] ) => void;
export const actions: [ ActionType, Action ][] = [
  // set spec
  [
    ActionType.SET_SPEC,
    ( store: Store ) => {
      const spec = es2021 as Spec;
      // set spec for scalaJS
      Scala_setSpec( JSON.stringify( spec ) );
      // load spec
      store.dispatch( loadSpec( spec ) );
    },
  ],
  // edit js
  [
    ActionType.EDIT_JS,
    ( store: Store, { code } ) => {
      store.dispatch( editJS( code ) );
    },
  ],
  // start debugger
  [
    ActionType.START_DBG,
    ( store: Store ) => {
      // get js code
      let state = store.getState();
      let code = state.js.code;
      // esparse & decode esparse result
      let compressed = new ESParse( "2021" ).parseWithCompress( code );
      // initalize state
      let IRState = Scala_initializeState( compressed );
      // create debugger
      let webDebugger = new Scala_WebDebugger( IRState );
      store.dispatch( loadDebugger( webDebugger ) );
    },
  ],
  // step
  [
    ActionType.STEP,
    ( store: Store ) => {
      // make debugger state busy and get debugger object
      store.dispatch( runDebugger() );
      let state = store.getState();
      let webDebugger = state.webDebugger.obj;

      // step
      webDebugger._step();

      // update ir info
      let algoName = webDebugger.getAlgoName();
      let line = webDebugger.getLine();
      let stackFrames = JSON.parse( webDebugger.getStackInfo() );
      store.dispatch( updateIrInfo( algoName, line, stackFrames ) );
      store.dispatch( pauseDebugger() );
    },
  ],
  // step-over
  [
    ActionType.STEP_OVER,
    ( store: Store ) => {
      // make debugger state busy and get debugger object
      store.dispatch( runDebugger() );
      let state = store.getState();
      let webDebugger = state.webDebugger.obj;

      // step-over
      webDebugger._stepOver();

      // update ir info
      let algoName = webDebugger.getAlgoName();
      let line = webDebugger.getLine();
      let stackFrames = JSON.parse( webDebugger.getStackInfo() );
      store.dispatch( updateIrInfo( algoName, line, stackFrames ) );
      store.dispatch( pauseDebugger() );
    },
  ],
  // step-out
  [
    ActionType.STEP_OUT,
    ( store: Store ) => {
      // make debugger state busy and get debugger object
      store.dispatch( runDebugger() );
      let state = store.getState();
      let webDebugger = state.webDebugger.obj;

      // step-out
      webDebugger._stepOut();

      // update ir info
      let algoName = webDebugger.getAlgoName();
      let line = webDebugger.getLine();
      let stackFrames = JSON.parse( webDebugger.getStackInfo() );
      store.dispatch( updateIrInfo( algoName, line, stackFrames ) );
      store.dispatch( pauseDebugger() );
    },
  ],
];
