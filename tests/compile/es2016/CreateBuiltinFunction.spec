        1. Assert: _realm_ is a Realm Record.
        1. Assert: _steps_ is either a set of algorithm steps or other definition of a function's behaviour provided in this specification.
        1. Let _func_ be a new built-in function object that when called performs the action described by _steps_. The new function object has internal slots whose names are the elements of _internalSlotsList_. The initial value of each of those internal slots is *undefined*.
        1. Set the [[Realm]] internal slot of _func_ to _realm_.
        1. Set the [[Prototype]] internal slot of _func_ to _prototype_.
        1. Set the [[Extensible]] internal slot of _func_ to *true*.
        1. Set the [[ScriptOrModule]] internal slot of _func_ to *null*.
        1. Return _func_.