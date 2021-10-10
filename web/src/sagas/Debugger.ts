import { call, put, select, takeLatest, all } from "redux-saga/effects";
import { toast } from "react-toastify";

import { ReduxState } from "../store";
import { AppState, move } from "../store/reducers/AppState";
import { doAPIPostRequest } from "../util/api";
import { DebuggerActionType } from "../store/reducers/Debugger";

// run debugger saga
function* runDebuggerSaga() {
  function* _runDebuggerSaga() {
    try {
      // get js code and run esparse
      // get breakpoints
      const state: ReduxState = yield select();
      const code = state.js.code;
      const breakpoints = state.webDebugger.breakpoints;
      const compressed = new ESParse("2021").parseWithCompress(code);
      console.log(code, breakpoints, compressed);
      // TODO run server debugger with js code and breakpoints
      yield call(() =>
        doAPIPostRequest("exec/run", {
          compressed,
          bps: JSON.stringify(breakpoints),
        })
      );
      // move app state to DEBUG_READY
      yield put(move(AppState.DEBUG_READY));
    } catch (e) {
      // show error toast
      toast.error(e);
    }
  }
  yield takeLatest(DebuggerActionType.RUN, _runDebuggerSaga);
}

export default function* debuggerSaga() {
  yield all([runDebuggerSaga()]);
}
