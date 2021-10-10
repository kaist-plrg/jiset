import { all } from "redux-saga/effects";

function* helloSaga() {
  console.log("Hello Sagas!");
}

function* helloSaga2() {
  console.log("Hello Sagas!");
}

export default function* rootSaga() {
  yield all([helloSaga(), helloSaga2()]);
}
