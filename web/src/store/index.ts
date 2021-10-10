import { createStore, applyMiddleware } from "redux";
import createSagaMiddleware from "redux-saga";

import rootReducer from "./reducers";
import rootSaga from "../sagas";

// create the saga middleware
const sagaMiddleware = createSagaMiddleware();

// create store from reducers
const store = createStore(rootReducer, applyMiddleware(sagaMiddleware));

// run the saga
sagaMiddleware.run(rootSaga);

export default store;

// store type definition
export type Store = typeof store;
export type ReduxState = ReturnType<typeof store.getState>;
export type Dispatch = typeof store.dispatch;
