import produce from "immer";

// redux actions
export enum IRActionType {
  UPDATE = "IRAction/UPDATE",
  SHOW_ALGO = "IRAction/SHOW_ALGO",
}
export function updateIrInfo ( stackFrame: [ string, number ][] ): IRAction {
  return {
    type: IRActionType.UPDATE,
    stackFrame,
  };
}
export function showAlgo ( idx: number ): IRAction {
  return {
    type: IRActionType.SHOW_ALGO,
    idx,
  };
}
export type IRAction =
  | {
    type: IRActionType.UPDATE;
    stackFrame: [ string, number ][];
  }
  | {
    type: IRActionType.SHOW_ALGO;
    idx: number;
  };

// redux state
type IRState = {
  stackFrame: {
    data: [ string, number ][];
    idx: number;
  };
};
const initialState: IRState = {
  stackFrame: {
    data: [],
    idx: 0,
  },
};

export default function reducer ( state = initialState, action: IRAction ) {
  switch ( action.type ) {
    case IRActionType.UPDATE:
      return produce( state, ( draft ) => {
        draft.stackFrame = {
          data: action.stackFrame,
          idx: 0,
        };
      } );
    case IRActionType.SHOW_ALGO:
      return produce( state, ( draft ) => {
        draft.stackFrame.idx = action.idx;
      } );
    default:
      return state;
  }
}
