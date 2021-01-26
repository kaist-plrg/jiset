        1. If Type(_value_) is String, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[StringData]] internal slot, then
          1. Assert: _value_.[[StringData]] is a String value.
          1. Return _value_.[[StringData]].
        1. Throw a *TypeError* exception.