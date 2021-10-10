import produce from "immer";
import { Breakpoint } from "../../object/Breakpoint";

// redux actions
export enum DebuggerActionType {
  RUN = "DebuggerAction/RUN",
  STOP = "DebuggerAction/STOP",
  CLEAR = "DebuggerAction/CLEAR",
  SPEC_STEP = "DebuggerAction/STEP",
  SPEC_STEP_OUT = "DebuggerAction/STEP_OUT",
  SPEC_STEP_OVER = "DebuggerAction/STEP_OVER",
  SPEC_CONTINUE = "DebuggerAction/SPEC_CONTINUE",
  // TODO
  TERMINATE = "DebuggerAction/TERMINATE",
  ADD_BREAK = "DebuggerAction/AD_BREAK",
  RM_BREAK = "DebuggerAction/RM_BREAK",
  TOGGLE_BREAK = "DebuggerAction/TOGGLE_BREAK",
}
export const run = (): DebuggerAction => ({
  type: DebuggerActionType.RUN,
});
export const stop = (): DebuggerAction => ({
  type: DebuggerActionType.STOP,
});
export const specStep = (): DebuggerAction => ({
  type: DebuggerActionType.SPEC_STEP,
});
export const specStepOver = (): DebuggerAction => ({
  type: DebuggerActionType.SPEC_STEP_OVER,
});
export const specStepOut = (): DebuggerAction => ({
  type: DebuggerActionType.SPEC_STEP_OUT,
});
export const specContinue = (): DebuggerAction => ({
  type: DebuggerActionType.SPEC_CONTINUE,
});
export const clearDebugger = (): DebuggerAction => ({
  type: DebuggerActionType.CLEAR,
});
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
      type: DebuggerActionType.RUN;
    }
  | {
      type: DebuggerActionType.STOP;
    }
  | {
      type: DebuggerActionType.SPEC_STEP;
    }
  | {
      type: DebuggerActionType.SPEC_STEP_OVER;
    }
  | {
      type: DebuggerActionType.SPEC_STEP_OUT;
    }
  | {
      type: DebuggerActionType.SPEC_CONTINUE;
    }
  | {
      type: DebuggerActionType.CLEAR;
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
  breakpoints: Breakpoint[];
};
const initialState: DebuggerState = {
  breakpoints: [],
};

// reducer
export default function reducer(state = initialState, action: DebuggerAction) {
  switch (action.type) {
    case DebuggerActionType.CLEAR:
      return produce(state, (draft) => {});
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
