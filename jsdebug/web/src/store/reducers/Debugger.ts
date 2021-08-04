import produce from "immer";

// redux actions
export enum DebuggerActionType {
  LOAD = "DebuggerAction/LOAD",
  RUN = "DebuggerAction/RUN",
  PAUSE = "DebuggerAction/PAUSE",
  CLEAR = "DebuggerAction/CLEAR",
}
export function loadDebugger ( obj: Scala_WebDebugger ): DebuggerAction {
  return {
    type: DebuggerActionType.LOAD,
    obj,
  };
}
export function clearDebugger (): DebuggerAction {
  return {
    type: DebuggerActionType.CLEAR,
  };
}
export function runDebugger (): DebuggerAction {
  return {
    type: DebuggerActionType.RUN,
  };
}
export function pauseDebugger (): DebuggerAction {
  return {
    type: DebuggerActionType.PAUSE,
  };
}
export type DebuggerAction =
  | {
    type: DebuggerActionType.LOAD;
    obj: Scala_WebDebugger;
  }
  | {
    type: DebuggerActionType.CLEAR;
  }
  | {
    type: DebuggerActionType.RUN;
  }
  | {
    type: DebuggerActionType.PAUSE;
  };

// redux state
type DebuggerState = {
  obj: Scala_WebDebugger;
  initialized: boolean;
  busy: boolean;
};
const INVALID_DEBUGGER: Scala_WebDebugger =
  undefined as unknown as Scala_WebDebugger;
const initialState: DebuggerState = {
  obj: INVALID_DEBUGGER,
  initialized: false,
  busy: false,
};

// reducer
export default function reducer ( state = initialState, action: DebuggerAction ) {
  switch ( action.type ) {
    case DebuggerActionType.CLEAR:
      return produce( state, ( draft ) => {
        draft.obj = INVALID_DEBUGGER;
        draft.initialized = false;
        draft.busy = false;
      } );
    case DebuggerActionType.LOAD:
      return produce( state, ( draft ) => {
        draft.obj = action.obj;
        draft.initialized = true;
        draft.busy = false;
      } );
    case DebuggerActionType.PAUSE:
      return produce( state, ( draft ) => {
        draft.busy = false;
      } );
    case DebuggerActionType.RUN:
      return produce( state, ( draft ) => {
        draft.busy = true;
      } );
    default:
      return state;
  }
}
