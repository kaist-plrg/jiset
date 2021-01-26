          1. Let _M_ be the *this* value.
          1. Perform ? RequireInternalSlot(_M_, [[WeakMapData]]).
          1. Let _entries_ be the List that is _M_.[[WeakMapData]].
          1. If Type(_key_) is not Object, return *undefined*.
          1. For each Record { [[Key]], [[Value]] } _p_ of _entries_, do
            1. If _p_.[[Key]] is not ~empty~ and SameValue(_p_.[[Key]], _key_) is *true*, return _p_.[[Value]].
          1. Return *undefined*.