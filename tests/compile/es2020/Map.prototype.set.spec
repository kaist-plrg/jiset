          1. Let _M_ be the *this* value.
          1. Perform ? RequireInternalSlot(_M_, [[MapData]]).
          1. Let _entries_ be the List that is _M_.[[MapData]].
          1. For each Record { [[Key]], [[Value]] } _p_ that is an element of _entries_, do
            1. If _p_.[[Key]] is not ~empty~ and SameValueZero(_p_.[[Key]], _key_) is *true*, then
              1. Set _p_.[[Value]] to _value_.
              1. Return _M_.
          1. If _key_ is *-0*, set _key_ to *+0*.
          1. Let _p_ be the Record { [[Key]]: _key_, [[Value]]: _value_ }.
          1. Append _p_ as the last element of _entries_.
          1. Return _M_.