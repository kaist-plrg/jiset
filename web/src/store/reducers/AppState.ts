import produce from "immer";

// app state
export enum AppState {
  INIT = "AppState/INIT",
  JS_INPUT = "AppState/JS_INPUT",
  TERMINATED = "AppState/TERMINATED",
  DEBUG_READY = "AppState/DEBUG_READY",
}

// redux actions
export enum AppStateActionType {
  MOVE = "AppStateAction/MOVE",
}
export function move(nextState: AppState): AppStateAction {
  return {
    type: AppStateActionType.MOVE,
    nextState,
  };
}
export type AppStateAction = {
  type: AppStateActionType.MOVE;
  nextState: AppState;
};

// redux state
type AppStateState = {
  state: AppState;
};
const initialState: AppStateState = { state: AppState.INIT };

// reducer
export default function reducer(state = initialState, action: AppStateAction) {
  switch (action.type) {
    case AppStateActionType.MOVE:
      return produce(state, (draft) => {
        draft.state = action.nextState;
      });
    default:
      return state;
  }
}
