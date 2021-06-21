        1. If Type(_value_) is String, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[StringData]] internal slot, then
          1. Let _s_ be _value_.[[StringData]].
          1. Assert: Type(_s_) is String.
          1. Return _s_.
        1. Throw a *TypeError* exception.