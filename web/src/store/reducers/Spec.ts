import { Spec } from "../../object/Spec";
import { getName } from "../../object/Algo";

// redux actions
export enum SpecActionType {
  LOAD_REQUEST = "SpecAction/LOAD_REQUEST",
  LOAD_SUCCESS = "SpecAction/LOAD_SUCCESS",
  LOAD_FAIL = "SpecAction/LOAD_FAIL",
}
export const loadSpecRequest = (): SpecAction => ({
  type: SpecActionType.LOAD_REQUEST,
});
export const loadSpecSuccess = (spec: Spec): SpecAction => ({
  type: SpecActionType.LOAD_SUCCESS,
  spec,
});
export const loadSpecFail = (err: unknown): SpecAction => ({
  type: SpecActionType.LOAD_FAIL,
  err,
});

export type SpecAction =
  | {
      type: SpecActionType.LOAD_REQUEST;
    }
  | {
      type: SpecActionType.LOAD_FAIL;
      err: unknown;
    }
  | {
      type: SpecActionType.LOAD_SUCCESS;
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
export default function reducer(state = initialState, action: SpecAction) {
  switch (action.type) {
    case SpecActionType.LOAD_SUCCESS: {
      state.spec = action.spec;
      state.algoNames = action.spec.algos.map((algo) => getName(algo));
      return state;
    }
    case SpecActionType.LOAD_FAIL: {
      console.error(action.err);
      return state;
    }
    default:
      return state;
  }
}
