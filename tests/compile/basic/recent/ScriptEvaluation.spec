        1. Let _globalEnv_ be _scriptRecord_.[[Realm]].[[GlobalEnv]].
        1. Let _scriptContext_ be a new ECMAScript code execution context.
        1. Set the Function of _scriptContext_ to *null*.
        1. Set the Realm of _scriptContext_ to _scriptRecord_.[[Realm]].
        1. Set the ScriptOrModule of _scriptContext_ to _scriptRecord_.
        1. Set the VariableEnvironment of _scriptContext_ to _globalEnv_.
        1. Set the LexicalEnvironment of _scriptContext_ to _globalEnv_.
        1. Suspend the currently running execution context.
        1. Push _scriptContext_ onto the execution context stack; _scriptContext_ is now the running execution context.
        1. Let _scriptBody_ be _scriptRecord_.[[ECMAScriptCode]].
        1. Let _result_ be GlobalDeclarationInstantiation(_scriptBody_, _globalEnv_).
        1. If _result_.[[Type]] is ~normal~, then
          1. Set _result_ to the result of evaluating _scriptBody_.
        1. If _result_.[[Type]] is ~normal~ and _result_.[[Value]] is ~empty~, then
          1. Set _result_ to NormalCompletion(*undefined*).
        1. Suspend _scriptContext_ and remove it from the execution context stack.
        1. Assert: The execution context stack is not empty.
        1. Resume the context that is now on the top of the execution context stack as the running execution context.
        1. Return Completion(_result_).