        1. If Type(_value_) is Symbol, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[SymbolData]] internal slot, then
          1. Let _s_ be _value_.[[SymbolData]].
          1. Assert: Type(_s_) is Symbol.
          1. Return _s_.
        1. Throw a *TypeError* exception.