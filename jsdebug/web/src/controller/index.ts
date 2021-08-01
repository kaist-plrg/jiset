import { StateMachine } from "./StateMachine";
import { actions } from "./Action";
import { transitions } from "./Transition";
import store from "../store";

// create global state machine
const sm = new StateMachine( transitions, actions, store );
export default sm;
