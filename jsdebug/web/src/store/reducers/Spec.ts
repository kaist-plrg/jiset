import { Spec } from "../../object/Spec";

// redux actions
export enum SpecActionType {
  LOAD = "SpecAction/LOAD",
}
export function loadSpec ( spec: Spec ): SpecAction {
  return {
    type: SpecActionType.LOAD,
    spec,
  };
}

export type SpecAction = {
  type: SpecActionType.LOAD;
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
export default function ( state = initialState, action: SpecAction ) {
  switch ( action.type ) {
    case SpecActionType.LOAD: {
      state.spec = action.spec;
      return state;
    }
    default:
      return state;
  }
}
