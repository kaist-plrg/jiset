          1. Let _M_ be the *this* value.
          1. Perform ? RequireInternalSlot(_M_, [[MapData]]).
          1. If IsCallable(_callbackfn_) is *false*, throw a *TypeError* exception.
          1. Let _entries_ be the List that is _M_.[[MapData]].
          1. For each Record { [[Key]], [[Value]] } _e_ that is an element of _entries_, in original key insertion order, do
            1. If _e_.[[Key]] is not ~empty~, then
              1. Perform ? Call(_callbackfn_, _thisArg_, « _e_.[[Value]], _e_.[[Key]], _M_ »).
          1. Return *undefined*.