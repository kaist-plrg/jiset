        1. If Type(_value_) is Symbol, return _value_.
        1. If Type(_value_) is Object and _value_ has a [[SymbolData]] internal slot, then
          1. Assert: _value_.[[SymbolData]] is a Symbol value.
          1. Return _value_.[[SymbolData]].
        1. Throw a *TypeError* exception.