          1. Let _M_ be the *this* value.
          1. If Type(_M_) is not Object, throw a *TypeError* exception.
          1. If _M_ does not have a [[WeakMapData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is the value of _M_'s [[WeakMapData]] internal slot.
          1. If Type(_key_) is not Object, throw a *TypeError* exception.
          1. Repeat for each Record {[[Key]], [[Value]]} _p_ that is an element of _entries_,
            1. If _p_.[[Key]] is not ~empty~ and SameValue(_p_.[[Key]], _key_) is *true*, then
              1. Set _p_.[[Value]] to _value_.
              1. Return _M_.
          1. Let _p_ be the Record {[[Key]]: _key_, [[Value]]: _value_}.
          1. Append _p_ as the last element of _entries_.
          1. Return _M_.