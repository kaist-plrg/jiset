        1. If Type(_value_) is Number, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[NumberData]] internal slot, then
          1. Assert: _value_'s [[NumberData]] internal slot is a Number value.
          1. Return the value of _value_'s [[NumberData]] internal slot.
        1. Throw a *TypeError* exception.