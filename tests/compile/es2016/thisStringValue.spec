        1. If Type(_value_) is String, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[StringData]] internal slot, then
          1. Assert: _value_'s [[StringData]] internal slot is a String value.
          1. Return the value of _value_'s [[StringData]] internal slot.
        1. Throw a *TypeError* exception.