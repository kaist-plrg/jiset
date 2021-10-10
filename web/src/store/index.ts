import rootReducer from "./reducers";
import { createStore } from "redux";

// create store from reducers
const store = createStore( rootReducer );
export default store;

// store type definition
export type Store = typeof store;
export type ReduxState = ReturnType<typeof store.getState>;
export type Dispatch = typeof store.dispatch;
