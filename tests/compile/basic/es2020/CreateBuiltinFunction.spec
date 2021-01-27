        1. Assert: _steps_ is either a set of algorithm steps or other definition of a function's behaviour provided in this specification.
        1. If _realm_ is not present, set _realm_ to the current Realm Record.
        1. Assert: _realm_ is a Realm Record.
        1. If _prototype_ is not present, set _prototype_ to _realm_.[[Intrinsics]].[[%Function.prototype%]].
        1. Let _func_ be a new built-in function object that when called performs the action described by _steps_. The new function object has internal slots whose names are the elements of _internalSlotsList_.
        1. Set _func_.[[Realm]] to _realm_.
        1. Set _func_.[[Prototype]] to _prototype_.
        1. Set _func_.[[Extensible]] to *true*.
        1. Set _func_.[[ScriptOrModule]] to *null*.
        1. Return _func_.