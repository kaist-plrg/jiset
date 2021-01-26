        1. Assert: Type(_O_) is Object.
        1. Assert: _level_ is either ~sealed~ or ~frozen~.
        1. Let _extensible_ be ? IsExtensible(_O_).
        1. If _extensible_ is *true*, return *false*.
        1. NOTE: If the object is extensible, none of its properties are examined.
        1. Let _keys_ be ? _O_.[[OwnPropertyKeys]]().
        1. For each element _k_ of _keys_, do
          1. Let _currentDesc_ be ? _O_.[[GetOwnProperty]](_k_).
          1. If _currentDesc_ is not *undefined*, then
            1. If _currentDesc_.[[Configurable]] is *true*, return *false*.
            1. If _level_ is ~frozen~ and IsDataDescriptor(_currentDesc_) is *true*, then
              1. If _currentDesc_.[[Writable]] is *true*, return *false*.
        1. Return *true*.