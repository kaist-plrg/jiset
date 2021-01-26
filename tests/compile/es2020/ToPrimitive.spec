        1. Assert: _input_ is an ECMAScript language value.
        1. If Type(_input_) is Object, then
          1. If _PreferredType_ is not present, let _hint_ be *"default"*.
          1. Else if _PreferredType_ is hint String, let _hint_ be *"string"*.
          1. Else,
            1. Assert: _PreferredType_ is hint Number.
            1. Let _hint_ be *"number"*.
          1. Let _exoticToPrim_ be ? GetMethod(_input_, @@toPrimitive).
          1. If _exoticToPrim_ is not *undefined*, then
            1. Let _result_ be ? Call(_exoticToPrim_, _input_, « _hint_ »).
            1. If Type(_result_) is not Object, return _result_.
            1. Throw a *TypeError* exception.
          1. If _hint_ is *"default"*, set _hint_ to *"number"*.
          1. Return ? OrdinaryToPrimitive(_input_, _hint_).
        1. Return _input_.