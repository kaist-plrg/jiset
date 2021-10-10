import { all } from "redux-saga/effects";

import specSaga from "./Spec";
import debuggerSaga from "./Debugger";

export default function* rootSaga() {
  yield all([specSaga(), debuggerSaga()]);
}
