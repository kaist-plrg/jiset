import produce from "immer";

// redux actions
export enum IRActionType {
  UPDATE = "IRAction/UPDATE",
}
export function updateIrInfo ( algoName: string, line: number, stackFrames: string[] ): IRAction {
  return {
    type: IRActionType.UPDATE,
    algoName,
    line,
    stackFrames,
  };
}
export type IRAction = {
  type: IRActionType.UPDATE;
  algoName: string;
  line: number;
  stackFrames: string[];
};

// redux state
type IRState = {
  algoName: string;
  line: number;
  stackFrames: string[];
};
export const INVALID_ALGO = "";
export const INVALID_LINE = -1;
const initialState: IRState = {
  algoName: INVALID_ALGO,
  line: INVALID_LINE,
  stackFrames: [],
};

export default function ( state = initialState, action: IRAction ) {
  switch ( action.type ) {
    case IRActionType.UPDATE:
      return produce( state, ( draft ) => {
        draft.algoName = action.algoName;
        draft.line = action.line;
        draft.stackFrames = action.stackFrames;
      } );
    default:
      return state;
  }
}
