import produce from "immer";

// redux actions
export enum JSActionType {
  EDIT = "JSAction/EDIT",
  CLEAR = "JSAction/CLEAR",
  UPDATE_RANGE = "JSAction/UPDATE_RANGE"
}
export function editJs ( code: string ): JSAction {
  return {
    type: JSActionType.EDIT,
    code,
  };
}
export function clearJs (): JSAction {
  return {
    type: JSActionType.CLEAR,
  };
}
export function updateJsRange ( start: number, end: number ): JSAction {
  return {
    type: JSActionType.UPDATE_RANGE,
    start,
    end
  };
}
export type JSAction =
  | {
    type: JSActionType.EDIT;
    code: string;
  }
  | {
    type: JSActionType.CLEAR;
  }
  | {
    type: JSActionType.UPDATE_RANGE;
    start: number;
    end: number;
  };

// redux state
type JSState = {
  code: string;
  start: number;
  end: number;
};
const initialState: JSState = {
  code: "var x = 1;\nvar y = 2;\nvar z = x + y;",
  start: -1,
  end: -1,
};

// reducer
export default function reducer ( state = initialState, action: JSAction ) {
  switch ( action.type ) {
    case JSActionType.EDIT:
      return produce( state, ( draft ) => {
        draft.code = action.code;
      } );
    case JSActionType.CLEAR:
      return produce( state, ( draft ) => {
        draft.start = -1;
        draft.end = -1;
      } );
    case JSActionType.UPDATE_RANGE:
      return produce( state, ( draft ) => {
        draft.start = action.start;
        draft.end = action.end;
      } );
    default:
      return state;
  }
}
