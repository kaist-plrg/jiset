          1. Let _S_ be the *this* value.
          1. Perform ? RequireInternalSlot(_S_, [[SetData]]).
          1. If IsCallable(_callbackfn_) is *false*, throw a *TypeError* exception.
          1. Let _entries_ be the List that is _S_.[[SetData]].
          1. For each _e_ that is an element of _entries_, in original insertion order, do
            1. If _e_ is not ~empty~, then
              1. Perform ? Call(_callbackfn_, _thisArg_, « _e_, _e_, _S_ »).
          1. Return *undefined*.