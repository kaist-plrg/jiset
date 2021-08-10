// IR State
declare class Scala_IRState { }

// IR Debugger
declare class Scala_WebDebugger {
  constructor ( state: Scala_IRState );
  // ir steps
  irStep (): Scala_StepResult;
  irStepOver (): Scala_StepResult;
  irStepOut (): Scala_StepResult;
  // spec steps
  specStep (): Scala_StepResult;
  specStepOver (): Scala_StepResult;
  specStepOut (): Scala_StepResult;
  // continue
  continueAlgo (): Scala_StepResult;
  // get state info
  getStackFrame (): string;
  getHeap (): string;
  getEnv (): string;
  getJsRange (): string;
  // breakpoints
  addAlgoBreak ( algoName: string, enabled: boolean = true ): void;
  rmAlgoBreak ( opt: string ): void;
  toggleAlgoBreak ( opt: string ): void;
}

// call setTarget
declare function Scala_setSpec ( str: string ): void;

// initialize debugger
declare function Scala_initializeState ( compressed: string ): Scala_IRState;
