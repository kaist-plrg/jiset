          1. Assert: Type(_newTarget_) is Undefined or Object.
          1. Let _callerContext_ be the running execution context.
          1. Let _calleeContext_ be a new ECMAScript code execution context.
          1. Set the Function of _calleeContext_ to _F_.
          1. Let _calleeRealm_ be the value of _F_'s [[Realm]] internal slot.
          1. Set the Realm of _calleeContext_ to _calleeRealm_.
          1. Set the ScriptOrModule of _calleeContext_ to the value of _F_'s [[ScriptOrModule]] internal slot.
          1. Let _localEnv_ be NewFunctionEnvironment(_F_, _newTarget_).
          1. Set the LexicalEnvironment of _calleeContext_ to _localEnv_.
          1. Set the VariableEnvironment of _calleeContext_ to _localEnv_.
          1. If _callerContext_ is not already suspended, suspend _callerContext_.
          1. Push _calleeContext_ onto the execution context stack; _calleeContext_ is now the running execution context.
          1. NOTE Any exception objects produced after this point are associated with _calleeRealm_.
          1. Return _calleeContext_.