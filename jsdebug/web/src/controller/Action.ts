import { Store } from "../store";
import { loadSpec } from "../store/reducers/Spec";
import es2021 from "../assets/es2021.json";
import { Spec } from "../object/Spec";

// possible action types
export enum ActionType {
  SET_SPEC = "ActionType/SET_SPEC",
  EDIT_JS = "ActionType/EDIT_JS",
  START_DBG = "ActionType/START_DBG",
  STEP = "ActionType/STEP",
  STEP_OVER = "ActionType/STEP_OVER",
  STEP_OUT = "ActionType/STEP_OUT",
  CONTINUE = "ActionType/CONTINUE",
  PAUSE = "ActionType/PAUSE",
  TERMINATE = "ActionType/TERMINATE",
  STOP_DBG = "ActionType/STOP_DBG",
}

// action payload
export type ActionPayload =
  | { type: ActionType.SET_SPEC }
  | {
      type: ActionType.EDIT_JS;
      source: string;
    }
  | { type: ActionType.START_DBG }
  | { type: ActionType.STEP }
  | { type: ActionType.STEP_OVER }
  | { type: ActionType.STEP_OUT }
  | { type: ActionType.CONTINUE }
  | { type: ActionType.PAUSE }
  | { type: ActionType.TERMINATE }
  | { type: ActionType.STOP_DBG };

// actions
export type Action = (store: Store) => void;
export const actions: [ActionType, Action][] = [
  // set spec
  [
    ActionType.SET_SPEC,
    (store: Store) => {
      const spec = es2021 as Spec;
      // set spec for scalaJS
      Scala_setSpec(JSON.stringify(spec));
      // load spec
      store.dispatch(loadSpec(spec));
    },
  ],
];
