          1. Let _M_ be the *this* value.
          1. Perform ? RequireInternalSlot(_M_, [[MapData]]).
          1. Let _entries_ be the List that is _M_.[[MapData]].
          1. For each Record { [[Key]], [[Value]] } _p_ of _entries_, do
            1. If _p_.[[Key]] is not ~empty~ and SameValueZero(_p_.[[Key]], _key_) is *true*, return *true*.
          1. Return *false*.