          1. Let _M_ be the *this* value.
          1. Perform ? RequireInternalSlot(_M_, [[WeakMapData]]).
          1. Let _entries_ be the List that is _M_.[[WeakMapData]].
          1. If Type(_key_) is not Object, return *false*.
          1. For each Record { [[Key]], [[Value]] } _p_ that is an element of _entries_, do
            1. If _p_.[[Key]] is not ~empty~ and SameValue(_p_.[[Key]], _key_) is *true*, then
              1. Set _p_.[[Key]] to ~empty~.
              1. Set _p_.[[Value]] to ~empty~.
              1. Return *true*.
          1. Return *false*.