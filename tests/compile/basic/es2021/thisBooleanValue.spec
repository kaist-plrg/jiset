        1. If Type(_value_) is Boolean, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[BooleanData]] internal slot, then
          1. Let _b_ be _value_.[[BooleanData]].
          1. Assert: Type(_b_) is Boolean.
          1. Return _b_.
        1. Throw a *TypeError* exception.