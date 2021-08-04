import { Store } from "../store";

// spec
import es2021 from "../assets/es2021.json";
import { loadSpec } from "../store/reducers/Spec";
import { Spec } from "../object/Spec";

// js
import { editJs, clearJs, updateJsRange } from "../store/reducers/JS";

// debugger
import {
  loadDebugger,
  runDebugger,
  pauseDebugger,
  clearDebugger,
  addBreak,
  rmBreak,
  ableBreak,
} from "../store/reducers/Debugger";

// ir
import { updateIrInfo, showAlgo, StackFrame, clearIr } from "../store/reducers/IR";

// possible action types
export enum ActionType {
  SET_SPEC = "ActionType/SET_SPEC",
  EDIT_JS = "ActionType/EDIT_JS",
  START_DBG = "ActionType/START_DBG",
  STEP = "ActionType/STEP",
  STEP_OVER = "ActionType/STEP_OVER",
  STEP_OUT = "ActionType/STEP_OUT",
  ADD_BREAK = "ActionType/ADD_BREAK",
  RM_BREAK = "ActionType/RM_BREAK",
  ABLE_BREAK = "ActionType/ABLE_BREAK",
  CONTINUE = "ActionType/CONTINUE",
  TERMINATE = "ActionType/TERMINATE",
  STOP_DBG = "ActionType/STOP_DBG",
  SHOW_ALGO = "ActionType/SHOW_ALGO",
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
  | {
    type: ActionType.ADD_BREAK;
    bpName: string;
  }
  | {
    type: ActionType.RM_BREAK;
    bpName: string;
  }
  | {
    type: ActionType.ABLE_BREAK;
    idx: number;
  }
  | { type: ActionType.CONTINUE }
  | { type: ActionType.TERMINATE }
  | { type: ActionType.STOP_DBG }
  | { type: ActionType.SHOW_ALGO; idx: number }

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
      store.dispatch( editJs( code ) );
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
  // show algorithm in stack frames
  [
    ActionType.SHOW_ALGO,
    ( store: Store, { idx } ) => {
      store.dispatch( showAlgo( idx ) );
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
      webDebugger.specStep();

      // update ir info
      let stackFrame: StackFrame = JSON.parse( webDebugger.getStackFrame() );
      let heap = JSON.parse( webDebugger.getHeap() );
      let [ jsStart, jsEnd ]: [ number, number ] = JSON.parse(
        webDebugger.getJsRange()
      );
      store.dispatch( updateJsRange( jsStart, jsEnd ) );
      store.dispatch( updateIrInfo( stackFrame, heap ) );
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
      webDebugger.specStepOver();

      // update ir info
      let stackFrame: StackFrame = JSON.parse( webDebugger.getStackFrame() );
      let heap = JSON.parse( webDebugger.getHeap() );
      let [ jsStart, jsEnd ]: [ number, number ] = JSON.parse(
        webDebugger.getJsRange()
      );
      store.dispatch( updateJsRange( jsStart, jsEnd ) );
      store.dispatch( updateIrInfo( stackFrame, heap ) );
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
      webDebugger.specStepOut();

      // update ir info
      let stackFrame: StackFrame = JSON.parse( webDebugger.getStackFrame() );
      let heap = JSON.parse( webDebugger.getHeap() );
      let [ jsStart, jsEnd ]: [ number, number ] = JSON.parse(
        webDebugger.getJsRange()
      );
      store.dispatch( updateJsRange( jsStart, jsEnd ) );
      store.dispatch( updateIrInfo( stackFrame, heap ) );
      store.dispatch( pauseDebugger() );
    },
  ],
  // cancel
  [
    ActionType.STOP_DBG,
    ( store: Store ) => {
      // clear all state except spec
      store.dispatch( clearDebugger() );
      store.dispatch( clearIr() );
      store.dispatch( clearJs() );
    }
  ],
  // add breakpoint
  [
    ActionType.ADD_BREAK,
    ( store: Store, { bpName } ) => {
      store.dispatch( addBreak( bpName ) );
    },
  ],
  // remove breakpoint
  [
    ActionType.RM_BREAK,
    ( store: Store, { bpName } ) => {
      store.dispatch( rmBreak( bpName ) );
    },
  ],
  // en/disable breakpoint
  [
    ActionType.ABLE_BREAK,
    ( store: Store, { idx } ) => {
      store.dispatch( ableBreak( idx ) );
    },
  ],
];
