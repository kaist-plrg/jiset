          1. Let _M_ be the *this* value.
          1. If Type(_M_) is not Object, throw a *TypeError* exception.
          1. If _M_ does not have a [[WeakMapData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is the value of _M_'s [[WeakMapData]] internal slot.
          1. If Type(_key_) is not Object, return *false*.
          1. Repeat for each Record {[[Key]], [[Value]]} _p_ that is an element of _entries_,
            1. If _p_.[[Key]] is not ~empty~ and SameValue(_p_.[[Key]], _key_) is *true*, return *true*.
          1. Return *false*.