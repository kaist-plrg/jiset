            1. Let _home_ be _envRec_.[[FunctionObject]].[[HomeObject]].
            1. If _home_ has the value *undefined*, return *undefined*.
            1. Assert: Type(_home_) is Object.
            1. Return ? _home_.[[GetPrototypeOf]]().