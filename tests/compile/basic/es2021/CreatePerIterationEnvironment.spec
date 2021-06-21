          1. If _perIterationBindings_ has any elements, then
            1. Let _lastIterationEnv_ be the running execution context's LexicalEnvironment.
            1. Let _outer_ be _lastIterationEnv_.[[OuterEnv]].
            1. Assert: _outer_ is not *null*.
            1. Let _thisIterationEnv_ be NewDeclarativeEnvironment(_outer_).
            1. For each element _bn_ of _perIterationBindings_, do
              1. Perform ! _thisIterationEnv_.CreateMutableBinding(_bn_, *false*).
              1. Let _lastValue_ be ? _lastIterationEnv_.GetBindingValue(_bn_, *true*).
              1. Perform _thisIterationEnv_.InitializeBinding(_bn_, _lastValue_).
            1. Set the running execution context's LexicalEnvironment to _thisIterationEnv_.
          1. Return *undefined*.