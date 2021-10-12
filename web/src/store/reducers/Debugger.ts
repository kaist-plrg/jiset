import produce from "immer";
import { Breakpoint } from "../../object/Breakpoint";

// redux actions
export enum DebuggerActionType {
  RUN = "DebuggerAction/RUN",
  STOP = "DebuggerAction/STOP",
  SPEC_STEP = "DebuggerAction/STEP",
  SPEC_STEP_OUT = "DebuggerAction/STEP_OUT",
  SPEC_STEP_OVER = "DebuggerAction/STEP_OVER",
  JS_STEP = "DebuggerAction/JS_STEP",
  JS_STEP_OUT = "DebuggerAction/JS_STEP_OUT",
  JS_STEP_OVER = "DebuggerAction/JS_STEP_OVER",
  SPEC_CONTINUE = "DebuggerAction/SPEC_CONTINUE",
  ADD_BREAK = "DebuggerAction/ADD_BREAK",
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
export const jsStep = (): DebuggerAction => ({
  type: DebuggerActionType.JS_STEP,
});
export const jsStepOver = (): DebuggerAction => ({
  type: DebuggerActionType.JS_STEP_OVER,
});
export const jsStepOut = (): DebuggerAction => ({
  type: DebuggerActionType.JS_STEP_OUT,
});
export const specContinue = (): DebuggerAction => ({
  type: DebuggerActionType.SPEC_CONTINUE,
});
export const addBreak = (bpName: string): DebuggerAction => ({
  type: DebuggerActionType.ADD_BREAK,
  bpName,
});
export const rmBreak = (opt: string): DebuggerAction => ({
  type: DebuggerActionType.RM_BREAK,
  opt,
});
export const toggleBreak = (opt: string): DebuggerAction => ({
  type: DebuggerActionType.TOGGLE_BREAK,
  opt,
});
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
      type: DebuggerActionType.JS_STEP;
    }
  | {
      type: DebuggerActionType.JS_STEP_OVER;
    }
  | {
      type: DebuggerActionType.JS_STEP_OUT;
    }
  | {
      type: DebuggerActionType.SPEC_CONTINUE;
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
    case DebuggerActionType.ADD_BREAK:
      return produce(state, (draft) => {
        console.log(action);
        draft.breakpoints.push({ name: action.bpName, enabled: true });
      });
    case DebuggerActionType.RM_BREAK:
      return produce(state, (draft) => {
        if (action.opt === "all") draft.breakpoints = [];
        else draft.breakpoints.splice(Number(action.opt), 1);
      });
    case DebuggerActionType.TOGGLE_BREAK:
      return produce(state, (draft) => {
        if (action.opt === "all")
          draft.breakpoints.forEach((bp) => (bp.enabled = !bp.enabled));
        else {
          let i = Number(action.opt);
          draft.breakpoints[i].enabled = !draft.breakpoints[i].enabled;
        }
      });
    default:
      return state;
  }
}
