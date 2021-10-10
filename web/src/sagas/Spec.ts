import { call, put, takeLatest } from "redux-saga/effects";

import {
  SpecActionType,
  loadSpecFail,
  loadSpecSuccess,
} from "../store/reducers/Spec";
import { AppState, move } from "../store/reducers/AppState";
import { doAPIGetRequest } from "../util/api";
import { Spec } from "../object/Spec";

// load spec saga
function* loadSpecSaga() {
  try {
    const spec: Spec = yield call(() => doAPIGetRequest("spec"));
    console.log("spec loadead", spec);
    yield put(loadSpecSuccess(spec));
    yield put(move(AppState.JS_INPUT));
  } catch (e) {
    yield put(loadSpecFail(e));
  }
}

export default function* specSaga() {
  yield takeLatest(SpecActionType.LOAD_REQUEST, loadSpecSaga);
}
