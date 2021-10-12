import { call, put, select, takeLatest, all } from "redux-saga/effects";
import { toast } from "react-toastify";

import { ReduxState } from "../store";
import { AppState, move } from "../store/reducers/AppState";
import { DebuggerAction, DebuggerActionType } from "../store/reducers/Debugger";
import { StackFrame, updateInfo, clearIR } from "../store/reducers/IR";
import { updateRange, clearJS } from "../store/reducers/JS";
import {
  doAPIPostRequest,
  doAPIDeleteRequest,
  doAPIPutRequest,
} from "../util/api";
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
        doAPIPostRequest("exec/run", { compressed, breakpoints })
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
  function* _stepBodySaga() {
    try {
      const { result, jsRanges, heap, stackFrames }: StepResult = yield call(
        () => doAPIPostRequest(endpoint)
      );
      if (result === StepResultType.TERMINATE) toast.success("Terminated");
      else if (result === StepResultType.BREAK) toast.info("Breaked");
      console.log(result);
      yield put(updateInfo(stackFrames, heap, []));
      yield put(updateRange(...jsRanges));
    } catch (e) {
      toast.error(e);
    }
  }
  return _stepBodySaga;
}

// spec step saga
function* specStepSaga() {
  yield takeLatest(DebuggerActionType.SPEC_STEP, mkStepSaga("exec/specStep"));
}
// spec step over saga
function* specStepOverSaga() {
  yield takeLatest(
    DebuggerActionType.SPEC_STEP_OVER,
    mkStepSaga("exec/specStepOver")
  );
}
// spec step over saga
function* specStepOutSaga() {
  yield takeLatest(
    DebuggerActionType.SPEC_STEP_OUT,
    mkStepSaga("exec/specStepOut")
  );
}
// spec continue saga
function* specContinueSaga() {
  yield takeLatest(
    DebuggerActionType.SPEC_CONTINUE,
    mkStepSaga("exec/specContinue")
  );
}

// js step saga
function* jsStepSaga() {
  yield takeLatest(DebuggerActionType.JS_STEP, mkStepSaga("exec/jsStep"));
}

// js step over saga
function* jsStepOverSaga() {
  yield takeLatest(
    DebuggerActionType.JS_STEP_OVER,
    mkStepSaga("exec/jsStepOver")
  );
}

// js step out saga
function* jsStepOutSaga() {
  yield takeLatest(
    DebuggerActionType.JS_STEP_OUT,
    mkStepSaga("exec/jsStepOut")
  );
}

// add breakpoint saga
function* addBreakSaga() {
  function* _addBreakSaga(action: DebuggerAction) {
    if (action.type !== DebuggerActionType.ADD_BREAK) return;
    let bp = { name: action.bpName, enabled: true };
    console.log(bp);
    yield call(() => doAPIPostRequest("breakpoint", bp));
  }
  yield takeLatest(DebuggerActionType.ADD_BREAK, _addBreakSaga);
}

// remove breakpoint saga
function* rmBreakSaga() {
  function* _rmBreakSaga(action: DebuggerAction) {
    if (action.type !== DebuggerActionType.RM_BREAK) return;
    yield call(() => doAPIDeleteRequest("breakpoint", action.opt));
  }
  yield takeLatest(DebuggerActionType.RM_BREAK, _rmBreakSaga);
}

// toggle breakpoint saga
function* toggleBreakSaga() {
  function* _toggleBreakSaga(action: DebuggerAction) {
    if (action.type !== DebuggerActionType.TOGGLE_BREAK) return;
    yield call(() => doAPIPutRequest("breakpoint", action.opt));
  }
  yield takeLatest(DebuggerActionType.TOGGLE_BREAK, _toggleBreakSaga);
}

// debugger sagas
export default function* debuggerSaga() {
  yield all([
    runSaga(),
    stopSaga(),
    specStepSaga(),
    specStepOverSaga(),
    specStepOutSaga(),
    jsStepSaga(),
    jsStepOverSaga(),
    jsStepOutSaga(),
    specContinueSaga(),
    addBreakSaga(),
    rmBreakSaga(),
    toggleBreakSaga(),
  ]);
}
