import { Transition } from "./Transition";
import { Store } from "../store";
import {
  Action,
  ActionPayload,
  ActionType,
  ActionDefinition,
  ACTION_NOP as nop,
  ExceptionHandler,
} from "./Action";
import { AppState } from "./AppState";
import { move } from "../store/reducers/Controller";

// state controller
export class StateMachine {
  readonly graph: Map<AppState, Map<ActionType, AppState>>;
  readonly actions: Map<ActionType, [ Action, ExceptionHandler ]>;
  readonly store: Store;
  private _state: AppState;
  private _verbose: boolean;

  // constructor
  constructor (
    transitions: Transition[],
    actions: ActionDefinition[],
    store: Store,
    verbose: boolean = true
  ) {
    // save redux store
    this.store = store;

    // graph contruction
    this.graph = new Map();
    transitions.forEach( ( { from, arrows } ) => this.graph.set( from, arrows ) );

    // set initial state
    this._state = AppState.INIT;

    // set actions based on action definition
    this.actions = new Map();
    actions.forEach( ( [ type, handlers, onError ] ) => {
      const chains = handlers.map( ( handler ) => handler( store ) );
      // compose action chain
      const action = chains.reduceRight( ( next, chain ) => chain( next ), nop );
      this.actions.set( type, [ action, onError ] );
    } );

    // verbose
    this._verbose = verbose;
  }

  // get current state
  getState (): AppState {
    return this._state;
  }

  // get next state
  private _getNextState ( type: ActionType ): AppState | undefined {
    const actions = this.graph.get( this._state );
    // if state is not defined, return undefined
    if ( actions === undefined ) return undefined;
    return actions.get( type );
  }

  move ( payload: ActionPayload ): void {
    // get next state and action
    const nextState = this._getNextState( payload.type );
    const actionDef = this.actions.get( payload.type );

    // measure time
    const timeLabel = `[StateMachine::move] ${ this._state } -- ${ payload.type } --> ${ nextState }`;
    if ( this._verbose ) console.time( timeLabel );

    // handle error of undefined behavior
    if ( actionDef === undefined || nextState === undefined ) {
      if ( actionDef === undefined )
        console.error(
          `[StateMachine::move] action definition not found: ${ payload.type }`
        );
      else
        console.error(
          `[StateMachine::move] undefined transition: ${ this._state } -- ${ payload.type } --> ???`
        );
      if ( this._verbose ) console.timeEnd( timeLabel );
      return;
    }

    // perform action
    const [ action, onError ] = actionDef;
    try {
      action( payload );
      // change redux controller state
      this._state = nextState;
      this.store.dispatch( move( nextState ) );
    } catch ( e ) {
      onError( e );
    }

    // log
    if ( this._verbose ) console.timeEnd( timeLabel );
  }
}
