          1. Let _S_ be the *this* value.
          1. Perform ? RequireInternalSlot(_S_, [[WeakSetData]]).
          1. If Type(_value_) is not Object, throw a *TypeError* exception.
          1. Let _entries_ be the List that is _S_.[[WeakSetData]].
          1. For each _e_ that is an element of _entries_, do
            1. If _e_ is not ~empty~ and SameValue(_e_, _value_) is *true*, then
              1. Return _S_.
          1. Append _value_ as the last element of _entries_.
          1. Return _S_.