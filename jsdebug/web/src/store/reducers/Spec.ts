import { Spec } from "../../object/Spec";
import { getName } from "../../object/Algo";

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
  algoNames: string[];
};
const initialState: SpecState = {
  spec: undefined,
  algoNames: [],
};

// reducer
export default function reducer ( state = initialState, action: SpecAction ) {
  switch ( action.type ) {
    case SpecActionType.LOAD: {
      state.spec = action.spec;
      state.algoNames = action.spec.algos.map( algo => getName( algo ) );
      return state;
    }
    default:
      return state;
  }
}
