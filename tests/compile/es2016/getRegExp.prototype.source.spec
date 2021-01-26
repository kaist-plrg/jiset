          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. If _R_ does not have an [[OriginalSource]] internal slot, throw a *TypeError* exception.
          1. If _R_ does not have an [[OriginalFlags]] internal slot, throw a *TypeError* exception.
          1. Let _src_ be the value of _R_'s [[OriginalSource]] internal slot.
          1. Let _flags_ be the value of _R_'s [[OriginalFlags]] internal slot.
          1. Return EscapeRegExpPattern(_src_, _flags_).