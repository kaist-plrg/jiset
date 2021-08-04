import produce from "immer";

// redux actions
export enum DebuggerActionType {
  LOAD = "DebuggerAction/LOAD",
  RUN = "DebuggerAction/RUN",
  PAUSE = "DebuggerAction/PAUSE",
  CLEAR = "DebuggerAction/CLEAR",
  TERMINATE = "DebuggerAction/TERMINATE",
  ADD_BREAK = "DebuggerAction/AD_BREAK",
  RM_BREAK = "DebuggerAction/RM_BREAK",
  ABLE_BREAK = "DebuggerAction/ABLE_BREAK",
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
export function addBreak ( bpName: string ): DebuggerAction {
  return {
    type: DebuggerActionType.ADD_BREAK,
    bpName,
  };
}
export function rmBreak ( bpName: string ): DebuggerAction {
  return {
    type: DebuggerActionType.RM_BREAK,
    bpName,
  };
}
export function ableBreak ( idx: number ): DebuggerAction {
  return {
    type: DebuggerActionType.ABLE_BREAK,
    idx,
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
  }
  | {
    type: DebuggerActionType.ADD_BREAK;
    bpName: string;
  }
  | {
    type: DebuggerActionType.RM_BREAK;
    bpName: string;
  }
  | {
    type: DebuggerActionType.ABLE_BREAK;
    idx: number;
  };

// redux state
type DebuggerState = {
  obj: Scala_WebDebugger;
  breakpoints: { name: string, enable: boolean }[];
  initialized: boolean;
  busy: boolean;
};
const INVALID_DEBUGGER: Scala_WebDebugger =
  undefined as unknown as Scala_WebDebugger;
const initialState: DebuggerState = {
  obj: INVALID_DEBUGGER,
  breakpoints: [],
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
      case DebuggerActionType.ADD_BREAK:
        return produce( state, ( draft ) => {
          let already = false;
          draft.breakpoints.forEach( bp => { if (bp.name === action.bpName) { already = true; }; } );
          if (!already) {
            let bp = { name: action.bpName, enable: true };
            draft.breakpoints.push(bp);
          };
        } );
      case DebuggerActionType.RM_BREAK:
        return produce( state, ( draft ) => {
          draft.breakpoints.forEach( bp => {
            if (bp.name === action.bpName) {
              let i = draft.breakpoints.indexOf(bp);
              draft.breakpoints.splice(i, 1);
            }; }
          );
        } );
        case DebuggerActionType.ABLE_BREAK:
          return produce( state, ( draft ) => {
            draft.breakpoints.forEach( bp => {
              let i = draft.breakpoints.indexOf(bp);
              if (i === action.idx) {
                // draft.breakpoints[i] = { name: action.bp.name, enable: !(action.bp.enable) };
                draft.breakpoints.splice(i, 1, { name: bp.name, enable: !(bp.enable) });
              }; }
            );
          } );
    default:
      return state;
  }
}
