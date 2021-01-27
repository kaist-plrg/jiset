          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. If _R_ does not have an [[OriginalFlags]] internal slot, throw a *TypeError* exception.
          1. Let _flags_ be the value of _R_'s [[OriginalFlags]] internal slot.
          1. If _flags_ contains the code unit `"g"`, return *true*.
          1. Return *false*.