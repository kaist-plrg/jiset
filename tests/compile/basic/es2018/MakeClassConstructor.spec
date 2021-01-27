        1. Assert: _F_ is an ECMAScript function object.
        1. Assert: _F_.[[FunctionKind]] is `"normal"`.
        1. Set _F_.[[FunctionKind]] to `"classConstructor"`.
        1. Return NormalCompletion(*undefined*).