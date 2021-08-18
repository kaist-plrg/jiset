import produce from "immer";

// redux actions
export enum JSActionType {
  EDIT = "JSAction/EDIT",
  CLEAR = "JSAction/CLEAR",
  UPDATE_RANGE = "JSAction/UPDATE_RANGE",
  ADD_BREAK = "JSAction/ADD_BREAK",
  RM_BREAK = "JSAction/RM_BREAK",
  TOGGLE_BREAK = "JSAction/TOGGLE_BREAK"
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
export function updateJsRange ( line: number, start: number, end: number ): JSAction {
  return {
    type: JSActionType.UPDATE_RANGE,
    line,
    start,
    end
  };
}
export function addBreakJs ( line: number ): JSAction {
  return {
    type: JSActionType.ADD_BREAK,
    line
  };
}
export function rmBreakJs ( opt: string ): JSAction {
  return {
    type: JSActionType.RM_BREAK,
    opt
  };
}
export function toggleBreakJs ( opt: string ): JSAction {
  return {
    type: JSActionType.TOGGLE_BREAK,
    opt
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
    line: number
    start: number;
    end: number;
  }
  | {
    type: JSActionType.ADD_BREAK;
    line: number;
  }
  | {
    type: JSActionType.RM_BREAK;
    opt: string;
  }
  | {
    type: JSActionType.TOGGLE_BREAK;
    opt: string;
  };

// redux state
type JSState = {
  code: string;
  line: number;
  start: number;
  end: number;
  breakpoints: { line: number, enable: boolean }[];
};
const initialState: JSState = {
  code: "var x = 1;\nvar y = 2;\nvar z = x + y;",
  line: 0,
  start: -1,
  end: -1,
  breakpoints: [],
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
        draft.line = 0;
        draft.start = -1;
        draft.end = -1;
      } );
    case JSActionType.UPDATE_RANGE:
      return produce( state, ( draft ) => {
        draft.line = action.line;
        draft.start = action.start;
        draft.end = action.end;
      } );
    case JSActionType.ADD_BREAK:
      return produce( state, ( draft ) => {
        let valid = state.breakpoints.every( ( { line } ) => line !== action.line );
        if ( valid ) {
          let bp = { line: action.line, enable: true };
          draft.breakpoints.push( bp );
          draft.breakpoints.sort( ( b1, b2 ) => b1.line - b2.line );
        }
      } );
    case JSActionType.RM_BREAK:
      return produce( state, ( draft ) => {
        if ( action.opt === "all" ) draft.breakpoints = [];
        else {
          let i = Number( action.opt );
          draft.breakpoints.splice( i, 1 );
        }
      } );
    case JSActionType.TOGGLE_BREAK:
      return produce( state, ( draft ) => {
        if ( action.opt === "all" )
          draft.breakpoints.forEach( bp => bp.enable = !bp.enable );
        else {
          let i = Number( action.opt );
          draft.breakpoints[ i ].enable = !draft.breakpoints[ i ].enable;
        }
      } );
    default:
      return state;
  }
}
