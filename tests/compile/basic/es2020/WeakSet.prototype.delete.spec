          1. Let _S_ be the *this* value.
          1. Perform ? RequireInternalSlot(_S_, [[WeakSetData]]).
          1. If Type(_value_) is not Object, return *false*.
          1. Let _entries_ be the List that is _S_.[[WeakSetData]].
          1. For each _e_ that is an element of _entries_, do
            1. If _e_ is not ~empty~ and SameValue(_e_, _value_) is *true*, then
              1. Replace the element of _entries_ whose value is _e_ with an element whose value is ~empty~.
              1. Return *true*.
          1. Return *false*.