import { all } from "redux-saga/effects";

import specSaga from "./Spec";

function* helloSaga() {
  console.log("Hello Sagas!");
}

export default function* rootSaga() {
  yield all([helloSaga(), specSaga()]);
}
