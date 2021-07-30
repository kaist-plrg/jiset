import { Spec } from "../../object/Spec";

// redux actions
export enum SpecActionType {
  LOAD_SPEC = "AppState/LOAD_SPEC",
}
export const loadSpec = (spec: Spec) => ({
  type: SpecActionType.LOAD_SPEC,
  spec,
});

export type SpecAction = {
  type: SpecActionType.LOAD_SPEC;
  spec: Spec;
};

// redux state
type SpecState = {
  spec: undefined | Spec;
};
const initialState: SpecState = {
  spec: undefined,
};

// reducer
export default function (state = initialState, action: SpecAction) {
  switch (action.type) {
    case SpecActionType.LOAD_SPEC: {
      state.spec = action.spec;
      return state;
    }
    default:
      return state;
  }
}
