          1. Let _s_ be the *this* value.
          1. If Type(_s_) is Symbol, let _sym_ be _s_.
          1. Else,
            1. If Type(_s_) is not Object, throw a *TypeError* exception.
            1. If _s_ does not have a [[SymbolData]] internal slot, throw a *TypeError* exception.
            1. Let _sym_ be the value of _s_'s [[SymbolData]] internal slot.
          1. Return SymbolDescriptiveString(_sym_).