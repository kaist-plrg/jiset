          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. If _R_ does not have an [[OriginalFlags]] internal slot, then
            1. If SameValue(_R_, %RegExpPrototype%) is *true*, return *undefined*.
            1. Otherwise, throw a *TypeError* exception.
          1. Let _flags_ be _R_.[[OriginalFlags]].
          1. If _flags_ contains the code unit 0x0069 (LATIN SMALL LETTER I), return *true*.
          1. Return *false*.