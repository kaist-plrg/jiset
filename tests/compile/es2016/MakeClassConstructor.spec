        1. Assert: _F_ is an ECMAScript function object.
        1. Assert: _F_'s [[FunctionKind]] internal slot is `"normal"`.
        1. Set _F_'s [[FunctionKind]] internal slot to `"classConstructor"`.
        1. Return NormalCompletion(*undefined*).