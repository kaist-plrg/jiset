          1. Let _S_ be the *this* value.
          1. If Type(_S_) is not Object, throw a *TypeError* exception.
          1. If _S_ does not have a [[SetData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is the value of _S_'s [[SetData]] internal slot.
          1. Repeat for each _e_ that is an element of _entries_,
            1. Replace the element of _entries_ whose value is _e_ with an element whose value is ~empty~.
          1. Return *undefined*.