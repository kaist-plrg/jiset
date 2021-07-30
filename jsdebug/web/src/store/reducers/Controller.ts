import produce from "immer";
import { AppState } from "../../controller/AppState";

// redux actions
export enum ControllerActionType {
  MOVE_STATE = "AppState/MOVE_STATE",
}
export const move = (nextState: AppState) => ({
  type: ControllerActionType.MOVE_STATE,
  nextState,
});
export type ControllerAction = {
  type: ControllerActionType.MOVE_STATE;
  nextState: AppState;
};

// redux state
type ControllerState = {
  state: AppState;
};
const initialState: ControllerState = { state: AppState.INIT };

// reducer
export default function (state = initialState, action: ControllerAction) {
  switch (action.type) {
    case ControllerActionType.MOVE_STATE:
      return produce(state, (draft) => {
        draft.state = action.nextState;
      });
    default:
      return state;
  }
}
