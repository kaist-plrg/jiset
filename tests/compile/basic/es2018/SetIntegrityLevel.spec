        1. Assert: Type(_O_) is Object.
        1. Assert: _level_ is either `"sealed"` or `"frozen"`.
        1. Let _status_ be ? _O_.[[PreventExtensions]]().
        1. If _status_ is *false*, return *false*.
        1. Let _keys_ be ? _O_.[[OwnPropertyKeys]]().
        1. If _level_ is `"sealed"`, then
          1. For each element _k_ of _keys_, do
            1. Perform ? DefinePropertyOrThrow(_O_, _k_, PropertyDescriptor { [[Configurable]]: *false* }).
        1. Else _level_ is `"frozen"`,
          1. For each element _k_ of _keys_, do
            1. Let _currentDesc_ be ? _O_.[[GetOwnProperty]](_k_).
            1. If _currentDesc_ is not *undefined*, then
              1. If IsAccessorDescriptor(_currentDesc_) is *true*, then
                1. Let _desc_ be the PropertyDescriptor { [[Configurable]]: *false* }.
              1. Else,
                1. Let _desc_ be the PropertyDescriptor { [[Configurable]]: *false*, [[Writable]]: *false* }.
              1. Perform ? DefinePropertyOrThrow(_O_, _k_, _desc_).
        1. Return *true*.