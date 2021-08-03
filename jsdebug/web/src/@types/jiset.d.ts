// IR State
declare class Scala_IRState { }

// IR Debugger
declare class Scala_WebDebugger {
  constructor ( state: Scala_IRState );
  // ir steps
  irStep (): void;
  irStepOver (): void;
  irStepOut (): void;
  // spec steps
  specStep (): void;
  specStepOver (): void;
  specStepOut (): void;
  // get state info
  getStackFrame (): string;
  getHeap (): string;
  getJsRange (): string;
}

// call setTarget
declare function Scala_setSpec ( str: string ): void;

// initialize debugger
declare function Scala_initializeState ( compressed: string ): Scala_IRState;
