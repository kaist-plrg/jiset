import produce from "immer";

// redux actions
export enum JSActionType {
  EDIT = "JSAction/EDIT",
  CLEAR = "JSAction/CLEAR",
}
export function editJS ( code: string ): JSAction {
  return {
    type: JSActionType.EDIT,
    code,
  };
}
export function clearJS (): JSAction {
  return {
    type: JSActionType.CLEAR,
  };
}
export type JSAction =
  | {
    type: JSActionType.EDIT;
    code: string;
  }
  | {
    type: JSActionType.CLEAR;
  };

// redux state
type JSState = {
  code: string;
};
const initialState: JSState = {
  code: "var x = 1 + 2;",
};

// reducer
export default function ( state = initialState, action: JSAction ) {
  switch ( action.type ) {
    case JSActionType.EDIT:
      return produce( state, ( draft ) => {
        draft.code = action.code;
      } );
    case JSActionType.CLEAR:
      return produce( state, ( draft ) => {
        draft.code = "";
      } );
    default:
      return state;
  }
}
