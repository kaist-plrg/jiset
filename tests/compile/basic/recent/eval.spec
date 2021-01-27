        1. Assert: The execution context stack has at least two elements.
        1. Let _callerContext_ be the second to top element of the execution context stack.
        1. Let _callerRealm_ be _callerContext_'s Realm.
        1. Return ? PerformEval(_x_, _callerRealm_, *false*, *false*).