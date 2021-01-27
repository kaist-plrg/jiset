        1. If the execution context stack is empty, return *null*.
        1. Let _ec_ be the topmost execution context on the execution context stack whose ScriptOrModule component is not *null*.
        1. If no such execution context exists, return *null*. Otherwise, return _ec_'s ScriptOrModule component.