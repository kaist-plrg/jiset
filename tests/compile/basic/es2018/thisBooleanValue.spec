        1. If Type(_value_) is Boolean, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[BooleanData]] internal slot, then
          1. Assert: _value_.[[BooleanData]] is a Boolean value.
          1. Return _value_.[[BooleanData]].
        1. Throw a *TypeError* exception.