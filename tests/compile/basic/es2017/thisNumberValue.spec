        1. If Type(_value_) is Number, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[NumberData]] internal slot, then
          1. Assert: _value_.[[NumberData]] is a Number value.
          1. Return _value_.[[NumberData]].
        1. Throw a *TypeError* exception.