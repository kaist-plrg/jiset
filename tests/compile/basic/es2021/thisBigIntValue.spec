        1. If Type(_value_) is BigInt, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[BigIntData]] internal slot, then
          1. Assert: Type(_value_.[[BigIntData]]) is BigInt.
          1. Return _value_.[[BigIntData]].
        1. Throw a *TypeError* exception.