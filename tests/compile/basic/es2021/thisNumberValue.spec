        1. If Type(_value_) is Number, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[NumberData]] internal slot, then
          1. Let _n_ be _value_.[[NumberData]].
          1. Assert: Type(_n_) is Number.
          1. Return _n_.
        1. Throw a *TypeError* exception.