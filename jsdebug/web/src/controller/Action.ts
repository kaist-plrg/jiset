import { Store } from "../store";
import { toast } from "react-toastify";

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
import {
  updateIrInfo,
  showAlgo,
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
  | { type: ActionType.SHOW_ALGO; idx: number };

// action definitions
// TODO type check action argument
export type ActionDefinition = [ActionType, ActionHandler[], ExceptionHandler];
export type ActionHandler = (store: Store) => ActionChain;
export type ActionChain = (next: Action) => Action;
export type Action = (...args: any[]) => void;
export type ExceptionHandler = (msg: string) => void;
export const ACTION_NOP: Action = () => {};

// common action handler
// debugger step pre action
export const stepPreAction: ActionHandler =
  (store: Store) => (next: Action) => () => {
    // make debugger state busy and get debugger object
    store.dispatch(runDebugger());
    let state = store.getState();
    next(state.webDebugger.obj);
  };
// debugger step post action
export const stepPostAction: ActionHandler =
  (store: Store) => (next: Action) => (webDebugger: Scala_WebDebugger) => {
    // update ir info and make debugger state not busy
    let stackFrame: StackFrame = JSON.parse(webDebugger.getStackFrame());
    let heap = JSON.parse(webDebugger.getHeap());
    let [jsStart, jsEnd]: [number, number] = JSON.parse(
      webDebugger.getJsRange()
    );
    store.dispatch(updateJsRange(jsStart, jsEnd));
    store.dispatch(updateIrInfo(stackFrame, heap));
    store.dispatch(pauseDebugger());
    next();
  };
// default exception handler
export const defaultExceptionHandler = (msg: string) => {
  console.error(msg);
  toast.error(msg);
};

export const actions: ActionDefinition[] = [
  // set spec
  [
    ActionType.SET_SPEC,
    [
      (store: Store) => () => () => {
        const spec = es2021 as Spec;
        // set spec for scalaJS
        Scala_setSpec(JSON.stringify(spec));
        // load spec
        store.dispatch(loadSpec(spec));
      },
    ],
    defaultExceptionHandler,
  ],
  // edit js code
  [
    ActionType.EDIT_JS,
    [
      (store: Store) =>
        () =>
        ({ code }) =>
          store.dispatch(editJs(code)),
    ],
    defaultExceptionHandler,
  ],
  // start debugger
  [
    ActionType.START_DBG,
    [
      (store: Store) => () => () => {
        // get js code
        let state = store.getState();
        let code = state.js.code;
        // esparse & decode esparse result
        let compressed: string;
        try {
          compressed = new ESParse("2021").parseWithCompress(code);
        } catch {
          throw "JavaScript Parsing Failed";
        }
        // initalize state
        let IRState = Scala_initializeState(compressed);
        // create debugger
        let webDebugger = new Scala_WebDebugger(IRState);
        store.dispatch(loadDebugger(webDebugger));
      },
    ],
    defaultExceptionHandler,
  ],
  // show algorithm in stack frames
  [
    ActionType.SHOW_ALGO,
    [
      (store: Store) =>
        () =>
        ({ idx }) =>
          store.dispatch(showAlgo(idx)),
    ],
    defaultExceptionHandler,
  ],
  // step
  [
    ActionType.STEP,
    [
      stepPreAction,
      () => (next: Action) => (webDebugger: Scala_WebDebugger) => {
        // step
        webDebugger.specStep();
        next(webDebugger);
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
      () => (next: Action) => (webDebugger: Scala_WebDebugger) => {
        webDebugger.specStepOver();
        next(webDebugger);
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
      () => (next: Action) => (webDebugger: Scala_WebDebugger) => {
        webDebugger.specStepOut();
        next(webDebugger);
      },
      stepPostAction,
    ],
    defaultExceptionHandler,
  ],
  // stop debugger
  [
    ActionType.STOP_DBG,
    [
      (store: Store) => () => () => {
        // clear all state except spec
        store.dispatch(clearDebugger());
        store.dispatch(clearIr());
        store.dispatch(clearJs());
      },
    ],
    defaultExceptionHandler,
  ],
  // add breakpoint
  [
    ActionType.ADD_BREAK,
    [
      (store: Store) =>
        () =>
        ({ bpName }) => {
          store.dispatch(addBreak(bpName));
        },
    ],
    defaultExceptionHandler,
  ],
  // remove breakpoint
  [
    ActionType.RM_BREAK,
    [
      (store: Store) =>
        () =>
        ({ bpName }) => {
          store.dispatch(rmBreak(bpName));
        },
    ],
    defaultExceptionHandler,
  ],
  // en/disable breakpoint
  [
    ActionType.ABLE_BREAK,
    [
      (store: Store) =>
        () =>
        ({ idx }) => {
          store.dispatch(ableBreak(idx));
        },
    ],
    defaultExceptionHandler,
  ],
];
