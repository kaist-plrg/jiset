import produce from "immer";
import { AppState } from "../../controller/AppState";

// redux actions
export enum ControllerActionType {
  MOVE = "AppState/MOVE",
}
export function move ( nextState: AppState ): ControllerAction {
  return {
    type: ControllerActionType.MOVE,
    nextState,
  };
}
export type ControllerAction = {
  type: ControllerActionType.MOVE;
  nextState: AppState;
};

// redux state
type ControllerState = {
  state: AppState;
};
const initialState: ControllerState = { state: AppState.INIT };

// reducer
export default function reducer ( state = initialState, action: ControllerAction ) {
  switch ( action.type ) {
    case ControllerActionType.MOVE:
      return produce( state, ( draft ) => {
        draft.state = action.nextState;
      } );
    default:
      return state;
  }
}
