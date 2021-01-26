          1. Let _M_ be the *this* value.
          1. If Type(_M_) is not Object, throw a *TypeError* exception.
          1. If _M_ does not have a [[MapData]] internal slot, throw a *TypeError* exception.
          1. Let _entries_ be the List that is _M_.[[MapData]].
          1. Let _count_ be 0.
          1. For each Record {[[Key]], [[Value]]} _p_ that is an element of _entries_, do
            1. If _p_.[[Key]] is not ~empty~, set _count_ to _count_+1.
          1. Return _count_.