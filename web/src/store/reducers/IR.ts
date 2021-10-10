import produce from "immer";

// name, beautified value
export type Environment = [string, string][];
// beautified addr and value
export type Heap = { [addr: string]: string };
// context name, current step number, env data
export type StackFrameData = [string, number, Environment];
export type StackFrame = StackFrameData[];

// redux actions
export enum IRActionType {
  UPDATE = "IRAction/UPDATE",
  SHOW_ALGO = "IRAction/SHOW_ALGO",
  SHOW_ENV = "IRAction/SHOW_ENV",
  CLEAR = "IRAction/CLEAR",
}
export const updateInfo = (
  stackFrame: StackFrame,
  heap: [string, string][],
  env: [string, string][][]
): IRAction => ({
  type: IRActionType.UPDATE,
  stackFrame,
  heap,
  env,
});
export const showAlgo = (idx: number): IRAction => ({
  type: IRActionType.SHOW_ALGO,
  idx,
});
export function showEnv(idx: number): IRAction {
  return {
    type: IRActionType.SHOW_ENV,
    idx,
  };
}
export function clearIr(): IRAction {
  return { type: IRActionType.CLEAR };
}
export type IRAction =
  | {
      type: IRActionType.UPDATE;
      stackFrame: StackFrame;
      heap: [string, string][];
      env: [string, string][][];
    }
  | {
      type: IRActionType.SHOW_ALGO;
      idx: number;
    }
  | {
      type: IRActionType.SHOW_ENV;
      idx: number;
    }
  | { type: IRActionType.CLEAR };

// redux state
type IRState = {
  stackFrame: {
    // stackframe data
    data: StackFrame;
    // stack frame index to show spec
    idx: number;
  };
  heap: Heap;
  env: {
    data: [string, string][][];
    idx: number;
  };
};
const initialState: IRState = {
  stackFrame: {
    data: [],
    idx: 0,
  },
  heap: {},
  env: {
    data: [],
    idx: 0,
  },
};

export default function reducer(state = initialState, action: IRAction) {
  switch (action.type) {
    case IRActionType.UPDATE:
      return produce(state, (draft) => {
        draft.stackFrame = {
          data: action.stackFrame,
          idx: 0,
        };
        for (var i = 0, h; i < action.heap.length; i++) {
          h = action.heap[i];
          draft.heap[h[0]] = h[1];
        }
        draft.env = {
          data: action.env,
          idx: 0,
        };
      });
    case IRActionType.SHOW_ALGO:
      return produce(state, (draft) => {
        draft.stackFrame.idx = action.idx;
      });
    case IRActionType.SHOW_ENV:
      return produce(state, (draft) => {
        draft.env.idx = action.idx;
      });
    case IRActionType.CLEAR:
      return produce(state, (draft) => {
        draft.stackFrame = initialState.stackFrame;
        draft.heap = initialState.heap;
        draft.env = initialState.env;
      });
    default:
      return state;
  }
}
