          1. Let _S_ be the *this* value.
          1. Perform ? RequireInternalSlot(_S_, [[SetData]]).
          1. Let _entries_ be the List that is _S_.[[SetData]].
          1. Let _count_ be 0.
          1. For each _e_ that is an element of _entries_, do
            1. If _e_ is not ~empty~, set _count_ to _count_ + 1.
          1. Return _count_.