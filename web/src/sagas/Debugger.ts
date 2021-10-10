import { call, put, select, takeLatest, all } from "redux-saga/effects";
import { toast } from "react-toastify";

import { ReduxState } from "../store";
import { AppState, move } from "../store/reducers/AppState";
import { DebuggerActionType, clearDebugger } from "../store/reducers/Debugger";
import { StackFrame, updateInfo, clearIR } from "../store/reducers/IR";
import { updateRange, clearJS } from "../store/reducers/JS";
import { doAPIPostRequest } from "../util/api";
import { StepResultType } from "../object/StepResult";

// run debugger saga
function* runSaga() {
  function* _runSaga() {
    try {
      // get code, breakpoints and run esparse
      const state: ReduxState = yield select();
      const code = state.js.code;
      const breakpoints = state.webDebugger.breakpoints;
      const compressed = new ESParse("2021").parseWithCompress(code);
      console.log(code, breakpoints, compressed);
      // run server debugger with js code and breakpoints
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
  yield takeLatest(DebuggerActionType.RUN, _runSaga);
}

// stop debugger saga
function* stopSaga() {
  function* _stopSaga() {
    yield put(clearIR());
    yield put(clearJS());
    yield put(clearDebugger());
    yield put(move(AppState.JS_INPUT));
  }
  yield takeLatest(DebuggerActionType.STOP, _stopSaga);
}

// step result type
type StepResult = {
  result: StepResultType;
  jsRanges: [number, number, number, number];
  heap: [string, string][];
  stackFrames: StackFrame;
};

// step body saga
function mkStepSaga(endpoint: string) {
  function* _specBodySaga() {
    try {
      const { result, jsRanges, heap, stackFrames }: StepResult = yield call(
        () => doAPIPostRequest(endpoint)
      );
      if (result === StepResultType.TERMINATE) toast.success("Terminated");
      else if (result === StepResultType.BREAK) toast.success("Breaked");
      console.log(result);
      yield put(updateInfo(stackFrames, heap, []));
      yield put(updateRange(...jsRanges));
    } catch (e) {
      toast.error(e);
    }
  }
  return _specBodySaga;
}

// spec step saga
function* specStepSaga() {
  yield takeLatest(DebuggerActionType.SPEC_STEP, mkStepSaga("exec/specStep"));
}
// spec step over
function* specStepOverSaga() {
  yield takeLatest(
    DebuggerActionType.SPEC_STEP_OVER,
    mkStepSaga("exec/specStepOver")
  );
}
// spec step over
function* specStepOutSaga() {
  yield takeLatest(
    DebuggerActionType.SPEC_STEP_OUT,
    mkStepSaga("exec/specStepOut")
  );
}

export default function* debuggerSaga() {
  yield all([
    runSaga(),
    stopSaga(),
    specStepSaga(),
    specStepOverSaga(),
    specStepOutSaga(),
  ]);
}
