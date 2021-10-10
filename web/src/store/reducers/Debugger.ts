import produce from "immer";

// redux actions
export enum DebuggerActionType {
  RUN = "DebuggerAction/RUN",
  PAUSE = "DebuggerAction/PAUSE",
  CLEAR = "DebuggerAction/CLEAR",
  TERMINATE = "DebuggerAction/TERMINATE",
  ADD_BREAK = "DebuggerAction/AD_BREAK",
  RM_BREAK = "DebuggerAction/RM_BREAK",
  TOGGLE_BREAK = "DebuggerAction/TOGGLE_BREAK",
}
export function clearDebugger(): DebuggerAction {
  return {
    type: DebuggerActionType.CLEAR,
  };
}
export function runDebugger(): DebuggerAction {
  return {
    type: DebuggerActionType.RUN,
  };
}
export function pauseDebugger(): DebuggerAction {
  return {
    type: DebuggerActionType.PAUSE,
  };
}
export function addBreak(bpName: string): DebuggerAction {
  return {
    type: DebuggerActionType.ADD_BREAK,
    bpName,
  };
}
export function rmBreak(opt: string): DebuggerAction {
  return {
    type: DebuggerActionType.RM_BREAK,
    opt,
  };
}
export function toggleBreak(opt: string): DebuggerAction {
  return {
    type: DebuggerActionType.TOGGLE_BREAK,
    opt,
  };
}
export type DebuggerAction =
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
      opt: string;
    }
  | {
      type: DebuggerActionType.TOGGLE_BREAK;
      opt: string;
    };

// redux state
type DebuggerState = {
  breakpoints: { name: string; enable: boolean }[];
  initialized: boolean;
  busy: boolean;
};
const initialState: DebuggerState = {
  breakpoints: [],
  initialized: false,
  busy: false,
};

// reducer
export default function reducer(state = initialState, action: DebuggerAction) {
  switch (action.type) {
    case DebuggerActionType.CLEAR:
      return produce(state, (draft) => {
        draft.initialized = false;
        draft.busy = false;
      });
    case DebuggerActionType.PAUSE:
      return produce(state, (draft) => {
        draft.busy = false;
      });
    case DebuggerActionType.RUN:
      return produce(state, (draft) => {
        draft.busy = true;
      });
    case DebuggerActionType.ADD_BREAK:
      return produce(state, (draft) => {
        let valid = state.breakpoints.every(
          ({ name }) => name !== action.bpName
        );
        if (valid) {
          let bp = { name: action.bpName, enable: true };
          draft.breakpoints.push(bp);
        }
      });
    case DebuggerActionType.RM_BREAK:
      return produce(state, (draft) => {
        if (action.opt === "all") draft.breakpoints = [];
        else draft.breakpoints.splice(Number(action.opt), 1);
      });
    case DebuggerActionType.TOGGLE_BREAK:
      return produce(state, (draft) => {
        if (action.opt === "all")
          draft.breakpoints.forEach((bp) => (bp.enable = !bp.enable));
        else {
          let i = Number(action.opt);
          draft.breakpoints[i].enable = !draft.breakpoints[i].enable;
        }
      });
    default:
      return state;
  }
}
