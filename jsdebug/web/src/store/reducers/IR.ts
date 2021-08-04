import produce from "immer";

// name, beautified value
export type Environment = [ string, string ][];
// beautified addr and value
export type Heap = { [ addr: string ]: string };
// context name, current step number, env data
export type StackFrameData = [ string, number, Environment ];
export type StackFrame = StackFrameData[]

// redux actions
export enum IRActionType {
  UPDATE = "IRAction/UPDATE",
  SHOW_ALGO = "IRAction/SHOW_ALGO",
  CLEAR = "IRAction/CLEAR",
}
export function updateIrInfo ( stackFrame: StackFrame, heap: Heap ): IRAction {
  return {
    type: IRActionType.UPDATE,
    stackFrame,
    heap
  };
}
export function showAlgo ( idx: number ): IRAction {
  return {
    type: IRActionType.SHOW_ALGO,
    idx,
  };
}
export function clearIr (): IRAction {
  return { type: IRActionType.CLEAR };
}
export type IRAction =
  | {
    type: IRActionType.UPDATE;
    stackFrame: StackFrame;
    heap: Heap;
  }
  | {
    type: IRActionType.SHOW_ALGO;
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
  heap: Heap
};
const initialState: IRState = {
  stackFrame: {
    data: [],
    idx: 0,
  },
  heap: {}
};

export default function reducer ( state = initialState, action: IRAction ) {
  switch ( action.type ) {
    case IRActionType.UPDATE:
      return produce( state, ( draft ) => {
        draft.stackFrame = {
          data: action.stackFrame,
          idx: 0,
        };
        draft.heap = action.heap;
      } );
    case IRActionType.SHOW_ALGO:
      return produce( state, ( draft ) => {
        draft.stackFrame.idx = action.idx;
      } );
    case IRActionType.CLEAR:
      return produce( state, ( draft ) => {
        draft.stackFrame = initialState.stackFrame;
        draft.heap = initialState.heap;
      } );
    default:
      return state;
  }
}
