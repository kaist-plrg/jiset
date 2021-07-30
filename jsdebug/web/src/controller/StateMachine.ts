import { Transition } from "./Transition";
import { Store } from "../store";
import { Action, ActionPayload, ActionType } from "./Action";
import { AppState } from "./AppState";
import { move } from "../store/reducers/Controller";

// state controller
export class StateMachine {
  readonly graph: Map<AppState, Map<ActionType, AppState>>;
  readonly actions: Map<ActionType, Action>;
  readonly store: Store;
  private _state: AppState;
  private _verbose: boolean;

  // constructor
  constructor(
    transitions: Transition[],
    actions: [ActionType, Action][],
    store: Store,
    verbose: boolean = true
  ) {
    // save redux store
    this.store = store;

    // graph contruction
    this.graph = new Map();
    transitions.forEach(({ from, arrows }) => this.graph.set(from, arrows));

    // set initial state
    this._state = AppState.INIT;

    // set actions
    this.actions = new Map();
    actions.forEach(([type, action]) => this.actions.set(type, action));

    // verbose
    this._verbose = verbose;
  }

  // get current state
  getState(): AppState {
    return this._state;
  }

  // get next state
  private _getNextState(type: ActionType): AppState | undefined {
    const actions = this.graph.get(this._state);
    // if state is not defined, return undefined
    if (actions === undefined) return undefined;
    return actions.get(type);
  }

  move(payload: ActionPayload): void {
    // get next state and action
    const nextState = this._getNextState(payload.type);
    const action = this.actions.get(payload.type);

    // TODO handle error
    if (action === undefined || nextState === undefined) return;

    // TODO exception handling
    action(this.store);

    // change redux controller state
    this.store.dispatch(move(nextState));

    // log
    if (this._verbose)
      console.log(
        `[StateMachine::move] ${this._state} -- ${payload.type} --> ${nextState}`
      );
  }
}
