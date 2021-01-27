          1. Let _s_ be the *this* value.
          1. If Type(_s_) is Symbol, return _s_.
          1. If Type(_s_) is not Object, throw a *TypeError* exception.
          1. If _s_ does not have a [[SymbolData]] internal slot, throw a *TypeError* exception.
          1. Return _s_.[[SymbolData]].