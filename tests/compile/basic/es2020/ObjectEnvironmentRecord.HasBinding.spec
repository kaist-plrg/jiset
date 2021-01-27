            1. Let _envRec_ be the object Environment Record for which the method was invoked.
            1. Let _bindings_ be the binding object for _envRec_.
            1. Let _foundBinding_ be ? HasProperty(_bindings_, _N_).
            1. If _foundBinding_ is *false*, return *false*.
            1. If the _withEnvironment_ flag of _envRec_ is *false*, return *true*.
            1. Let _unscopables_ be ? Get(_bindings_, @@unscopables).
            1. If Type(_unscopables_) is Object, then
              1. Let _blocked_ be ! ToBoolean(? Get(_unscopables_, _N_)).
              1. If _blocked_ is *true*, return *false*.
            1. Return *true*.