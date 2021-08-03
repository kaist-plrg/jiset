// IR State
declare class Scala_IRState { }

// IR Debugger
declare class Scala_WebDebugger {
  constructor ( state: Scala_IRState );
  _step (): void;
  _stepOver (): void;
  _stepOut (): void;
  getStackFrame (): string;
}

// call setTarget
declare function Scala_setSpec ( str: string ): void;

// initialize debugger
declare function Scala_initializeState ( compressed: string ): Scala_IRState;
