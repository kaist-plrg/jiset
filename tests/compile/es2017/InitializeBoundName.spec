          1. Assert: Type(_name_) is String.
          1. If _environment_ is not *undefined*, then
            1. Let _env_ be the EnvironmentRecord component of _environment_.
            1. Perform _env_.InitializeBinding(_name_, _value_).
            1. Return NormalCompletion(*undefined*).
          1. Else,
            1. Let _lhs_ be ResolveBinding(_name_).
            1. Return ? PutValue(_lhs_, _value_).