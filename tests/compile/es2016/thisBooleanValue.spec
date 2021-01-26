          1. If Type(_value_) is Boolean, return _value_.
          1. If Type(_value_) is Object and _value_ has a [[BooleanData]] internal slot, then
            1. Assert: _value_'s [[BooleanData]] internal slot is a Boolean value.
            1. Return the value of _value_'s [[BooleanData]] internal slot.
          1. Throw a *TypeError* exception.