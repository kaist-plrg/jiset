import { ActionType } from "./Action";
import { AppState } from "./AppState";

// transitions
export interface Transition {
  from: AppState;
  arrows: Map<ActionType, AppState>;
}
function mkTransition(
  from: AppState,
  pairs: [ActionType, AppState][]
): Transition {
  const arrows = new Map<ActionType, AppState>();
  pairs.forEach(([action, to]) => arrows.set(action, to));
  return { from, arrows };
}
export const transitions: Transition[] = [
  mkTransition(AppState.INIT, [[ActionType.SET_SPEC, AppState.JS_INPUT]]),
  mkTransition(AppState.JS_INPUT, [
    [ActionType.EDIT_JS, AppState.JS_INPUT],
    [ActionType.START_DBG, AppState.DEBUG_READY],
  ]),
  mkTransition(AppState.DEBUG_READY, [
    [ActionType.STEP, AppState.DEBUG_READY],
    [ActionType.STEP_OVER, AppState.DEBUG_READY],
    [ActionType.STEP_OUT, AppState.DEBUG_READY],
    [ActionType.CONTINUE, AppState.DEBUG_READY],
    [ActionType.TERMINATE, AppState.TERMINATED],
  ]),
  mkTransition(AppState.TERMINATED, [[ActionType.STOP_DBG, AppState.JS_INPUT]]),
];
