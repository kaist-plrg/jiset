        1. Let _globalEnv_ be _scriptRecord_.[[Realm]].[[GlobalEnv]].
        1. Let _scriptCxt_ be a new ECMAScript code execution context.
        1. Set the Function of _scriptCxt_ to *null*.
        1. Set the Realm of _scriptCxt_ to _scriptRecord_.[[Realm]].
        1. Set the ScriptOrModule of _scriptCxt_ to _scriptRecord_.
        1. Set the VariableEnvironment of _scriptCxt_ to _globalEnv_.
        1. Set the LexicalEnvironment of _scriptCxt_ to _globalEnv_.
        1. Suspend the currently running execution context.
        1. Push _scriptCxt_ on to the execution context stack; _scriptCxt_ is now the running execution context.
        1. Let _result_ be GlobalDeclarationInstantiation(|ScriptBody|, _globalEnv_).
        1. If _result_.[[Type]] is ~normal~, then
          1. Let _result_ be the result of evaluating |ScriptBody|.
        1. If _result_.[[Type]] is ~normal~ and _result_.[[Value]] is ~empty~, then
          1. Let _result_ be NormalCompletion(*undefined*).
        1. Suspend _scriptCxt_ and remove it from the execution context stack.
        1. Assert: the execution context stack is not empty.
        1. Resume the context that is now on the top of the execution context stack as the running execution context.
        1. Return Completion(_result_).