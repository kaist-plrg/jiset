        1. If the execution context stack is empty, return *null*.
        1. Let _ec_ be the topmost execution context on the execution context stack whose Function component's [[ScriptOrModule]] component is not *null*.
        1. If such an execution context exists, return _ec_'s Function component's [[ScriptOrModule]] slot's value.
        1. Otherwise, let _ec_ be the running execution context.
        1. Assert: _ec_'s ScriptOrModule component is not *null*.
        1. Return _ec_'s ScriptOrModule component.