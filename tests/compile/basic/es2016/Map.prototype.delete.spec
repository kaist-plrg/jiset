          1. Let _M_ be the *this* value.
          1. If Type(_M_) is not Object, throw a *TypeError* exception.
          1. If _M_ does not have a [[MapData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is the value of _M_'s [[MapData]] internal slot.
          1. Repeat for each Record {[[Key]], [[Value]]} _p_ that is an element of _entries_,
            1. If _p_.[[Key]] is not ~empty~ and SameValueZero(_p_.[[Key]], _key_) is *true*, then
              1. Set _p_.[[Key]] to ~empty~.
              1. Set _p_.[[Value]] to ~empty~.
              1. Return *true*.
          1. Return *false*.