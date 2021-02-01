          1. Let _S_ be the *this* value.
          1. If Type(_S_) is not Object, throw a *TypeError* exception.
          1. If _S_ does not have a [[SetData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is the value of _S_'s [[SetData]] internal slot.
          1. Repeat for each _e_ that is an element of _entries_,
            1. If _e_ is not ~empty~ and SameValueZero(_e_, _value_) is *true*, return *true*.
          1. Return *false*.