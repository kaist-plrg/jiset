import { Store } from "../store";
import { toast } from "react-toastify";
import { ActionError } from "../errors";

// spec
import es2021 from "../assets/es2021.json";
import { loadSpec } from "../store/reducers/Spec";
import { Spec } from "../object/Spec";

// js
import {
  editJs,
  clearJs,
  updateJsRange,
  addBreakJs,
  rmBreakJs,
  toggleBreakJs
} from "../store/reducers/JS";

// debugger
import {
  loadDebugger,
  runDebugger,
  pauseDebugger,
  clearDebugger,
  addBreak,
  rmBreak,
  toggleBreak,
} from "../store/reducers/Debugger";
import { Scala_StepResult } from "../object/StepResult";

// ir
import {
  updateIrInfo,
  showAlgo,
  showEnv,
  StackFrame,
  clearIr,
} from "../store/reducers/IR";

// possible action types
export enum ActionType {
  SET_SPEC = "ActionType/SET_SPEC",
  EDIT_JS = "ActionType/EDIT_JS",
  START_DBG = "ActionType/START_DBG",
  STEP = "ActionType/STEP",
  STEP_OVER = "ActionType/STEP_OVER",
  STEP_OUT = "ActionType/STEP_OUT",
  STEP_LINE = "ActionType/STEP_LINE",
  CONTINUE = "ActionType/CONTINUE",
  ADD_BREAK = "ActionType/ADD_BREAK",
  RM_BREAK = "ActionType/RM_BREAK",
  TOGGLE_BREAK = "ActionType/TOGGLE_BREAK",
  ADD_BREAK_JS = "ActionType/ADD_BREAK_JS",
  RM_BREAK_JS = "ActionType/RM_BREAK_JS",
  TOGGLE_BREAK_JS = "ActionType/TOGGLE_BREAK_JS",
  TERMINATE = "ActionType/TERMINATE",
  STOP_DBG = "ActionType/STOP_DBG",
  SHOW_ALGO = "ActionType/SHOW_ALGO",
  SHOW_ENV = "ActionType/SHOW_ENV",
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
  | { type: ActionType.STEP_LINE }
  | { type: ActionType.CONTINUE }
  | {
    type: ActionType.ADD_BREAK;
    bpName: string;
  }
  | {
    type: ActionType.RM_BREAK;
    opt: string;
  }
  | {
    type: ActionType.TOGGLE_BREAK;
    opt: string;
  }
  | {
    type: ActionType.ADD_BREAK_JS;
    line: number;
  }
  | {
    type: ActionType.RM_BREAK_JS;
    opt: string;
  }
  | {
    type: ActionType.TOGGLE_BREAK_JS;
    opt: string;
  }
  | { type: ActionType.TERMINATE }
  | { type: ActionType.STOP_DBG }
  | { type: ActionType.SHOW_ALGO; idx: number }
  | { type: ActionType.SHOW_ENV; idx: number };

// action definitions
// TODO type check action argument
export type ActionDefinition = [ ActionType, ActionHandler[], ExceptionHandler ];
export type ActionHandler = ( store: Store ) => ActionChain;
export type ActionChain = ( next: Action ) => Action;
export type Action = ( ...args: any[] ) => void;
export type ExceptionHandler = ( e: ActionError ) => void;
export const ACTION_NOP: Action = () => { };

// common action handler
// debugger step pre action
export const stepPreAction: ActionHandler =
  ( store: Store ) => ( next: Action ) => () => {
    // make debugger state busy and get debugger object
    store.dispatch( runDebugger() );
    let state = store.getState();
    next( state.webDebugger.obj );
  };
// debugger step post action
export const stepPostAction: ActionHandler =
  ( store: Store ) =>
    ( next: Action ) =>
      ( res: Scala_StepResult, webDebugger: Scala_WebDebugger ) => {
        // if result is BREAK or TERMINATE, show toast message
        if ( res === Scala_StepResult.TERMINATE ) toast.success( "Terminated" );
        else if ( res === Scala_StepResult.BREAK ) toast.info( "Breaked" );
        // update ir info and make debugger state not busy
        let stackFrame: StackFrame = JSON.parse( webDebugger.getStackFrame() );
        let heap = JSON.parse( webDebugger.getHeap() );
        let env = JSON.parse( webDebugger.getEnv() );
        let [ jsLineFrom, jsLineTo, jsStart, jsEnd ]: [ number, number, number, number ] = JSON.parse(
          webDebugger.getJsRange()
        );
        console.log(`${jsLineFrom} - ${jsLineTo}`);
        store.dispatch( updateJsRange( jsLineFrom, jsLineTo, jsStart, jsEnd ) );
        store.dispatch( updateIrInfo( stackFrame, heap, env ) );
        store.dispatch( pauseDebugger() );
        next();
      };
// default exception handler
export const defaultExceptionHandler = ( e: ActionError ) => {
  console.error( e );
  toast.error( e.message );
};

export const actions: ActionDefinition[] = [
  // set spec
  [
    ActionType.SET_SPEC,
    [
      ( store: Store ) => () => () => {
        const spec = es2021 as Spec;
        // set spec for scalaJS
        Scala_setSpec( JSON.stringify( spec ) );
        // load spec
        store.dispatch( loadSpec( spec ) );
      },
    ],
    defaultExceptionHandler,
  ],
  // edit js code
  [
    ActionType.EDIT_JS,
    [
      ( store: Store ) =>
        () =>
          ( { code } ) =>
            store.dispatch( editJs( code ) ),
    ],
    defaultExceptionHandler,
  ],
  // start debugger
  [
    ActionType.START_DBG,
    [
      ( store: Store ) => () => () => {
        // get js code
        let state = store.getState();
        let code = state.js.code;
        let breakpoints = state.webDebugger.breakpoints;
        let breakpointsJS = state.js.breakpoints;
        // esparse & decode esparse result
        let compressed: string;
        try {
          compressed = new ESParse( "2021" ).parseWithCompress( code );
        } catch ( e ) {
          console.error( e );
          throw new ActionError( "JavaScript Parsing Fail!" );
        }
        // initalize state
        let IRState = Scala_initializeState( compressed );
        // create debugger
        let webDebugger = new Scala_WebDebugger( IRState );
        // add breakpoints
        breakpoints.forEach( ( bp ) => {
          webDebugger.addAlgoBreak( bp.name, bp.enable );
        } );
        breakpointsJS.forEach( ( bp ) => {
          webDebugger.addJSBreak( bp.line, bp.enable );
        } );
        store.dispatch( loadDebugger( webDebugger ) );
      },
    ],
    defaultExceptionHandler,
  ],
  // show algorithm in stack frames
  [
    ActionType.SHOW_ALGO,
    [
      ( store: Store ) =>
        () =>
          ( { idx } ) =>
            store.dispatch( showAlgo( idx ) ),
    ],
    defaultExceptionHandler,
  ],
  // show algorithm in stack frames
  [
    ActionType.SHOW_ENV,
    [
      ( store: Store ) =>
        () =>
          ( { idx } ) =>
            store.dispatch( showEnv( idx ) ),
    ],
    defaultExceptionHandler,
  ],
  // step
  [
    ActionType.STEP,
    [
      stepPreAction,
      () => ( next: Action ) => ( webDebugger: Scala_WebDebugger ) => {
        // step
        const res = webDebugger.specStep();
        next( res, webDebugger );
      },
      stepPostAction,
    ],
    defaultExceptionHandler,
  ],
  // step-over
  [
    ActionType.STEP_OVER,
    [
      stepPreAction,
      () => ( next: Action ) => ( webDebugger: Scala_WebDebugger ) => {
        const res = webDebugger.specStepOver();
        next( res, webDebugger );
      },
      stepPostAction,
    ],
    defaultExceptionHandler,
  ],
  // step-out
  [
    ActionType.STEP_OUT,
    [
      stepPreAction,
      () => ( next: Action ) => ( webDebugger: Scala_WebDebugger ) => {
        const res = webDebugger.specStepOut();
        next( res, webDebugger );
      },
      stepPostAction,
    ],
    defaultExceptionHandler,
  ],
  // step-line
  [
    ActionType.STEP_LINE,
    [
      stepPreAction,
      () => ( next: Action ) => ( webDebugger: Scala_WebDebugger ) => {
        // step
        const res = webDebugger.jsStep();
        next( res, webDebugger );
      },
      stepPostAction,
    ],
    defaultExceptionHandler,
  ],
  // continue
  [
    ActionType.CONTINUE,
    [
      stepPreAction,
      () => ( next: Action ) => ( webDebugger: Scala_WebDebugger ) => {
        // continue
        const res = webDebugger.continueAlgo();
        next( res, webDebugger );
      },
      stepPostAction,
    ],
    defaultExceptionHandler,
  ],
  // stop debugger
  [
    ActionType.STOP_DBG,
    [
      ( store: Store ) => () => () => {
        // clear all state except spec
        store.dispatch( clearDebugger() );
        store.dispatch( clearIr() );
        store.dispatch( clearJs() );
      },
    ],
    defaultExceptionHandler,
  ],
  // add breakpoint
  [
    ActionType.ADD_BREAK,
    [
      ( store: Store ) =>
        () =>
          ( { bpName } ) => {
            // add breakpoint by algorithm name
            let state = store.getState();
            let webDebugger = state.webDebugger.obj;
            webDebugger.addAlgoBreak( bpName );
            store.dispatch( addBreak( bpName ) );
          },
    ],
    defaultExceptionHandler,
  ],
  // remove breakpoint
  [
    ActionType.RM_BREAK,
    [
      ( store: Store ) =>
        () =>
          ( { opt } ) => {
            // remove breakpoint
            let state = store.getState();
            let webDebugger = state.webDebugger.obj;
            webDebugger.rmAlgoBreak( opt );
            store.dispatch( rmBreak( opt ) );
          },
    ],
    defaultExceptionHandler,
  ],
  // toggle breakpoint
  [
    ActionType.TOGGLE_BREAK,
    [
      ( store: Store ) =>
        () =>
          ( { opt } ) => {
            // toggle breakpoint
            let state = store.getState();
            let webDebugger = state.webDebugger.obj;
            webDebugger.toggleAlgoBreak( opt );
            store.dispatch( toggleBreak( opt ) );
          },
    ],
    defaultExceptionHandler,
  ],
  // add JS breakpoint
  [
    ActionType.ADD_BREAK_JS,
    [
      ( store: Store ) =>
        () =>
          ( { line } ) => {
            // add breakpoint by algorithm name
            let state = store.getState();
            let webDebugger = state.webDebugger.obj;
            webDebugger.addJSBreak( line );
            store.dispatch( addBreakJs( line ) );
          },
    ],
    defaultExceptionHandler,
  ],
  // remove JS breakpoint
  [
    ActionType.RM_BREAK_JS,
    [
      ( store: Store ) =>
        () =>
          ( { opt } ) => {
            // remove breakpoint
            let state = store.getState();
            let webDebugger = state.webDebugger.obj;
            webDebugger.rmJSBreak( opt );
            store.dispatch( rmBreakJs( opt ) );
          },
    ],
    defaultExceptionHandler,
  ],
  // toggle JS breakpoint
  [
    ActionType.TOGGLE_BREAK_JS,
    [
      ( store: Store ) =>
        () =>
          ( { opt } ) => {
            // toggle breakpoint
            let state = store.getState();
            let webDebugger = state.webDebugger.obj;
            webDebugger.toggleJSBreak( opt );
            store.dispatch( toggleBreakJs( opt ) );
          },
    ],
    defaultExceptionHandler,
  ],
];
