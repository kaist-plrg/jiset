            1. Let _envRec_ be the function Environment Record for which the method was invoked.
            1. Let _home_ be the value of _envRec_.[[HomeObject]].
            1. If _home_ has the value *undefined*, return *undefined*.
            1. Assert: Type(_home_) is Object.
            1. Return ? _home_.[[GetPrototypeOf]]().