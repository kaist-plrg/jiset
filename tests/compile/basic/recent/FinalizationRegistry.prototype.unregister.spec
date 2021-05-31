          1. Let _finalizationRegistry_ be the *this* value.
          1. Perform ? RequireInternalSlot(_finalizationRegistry_, [[Cells]]).
          1. If Type(_unregisterToken_) is not Object, throw a *TypeError* exception.
          1. Let _removed_ be *false*.
          1. For each Record { [[WeakRefTarget]], [[HeldValue]], [[UnregisterToken]] } _cell_ of _finalizationRegistry_.[[Cells]], do
            1. If _cell_.[[UnregisterToken]] is not ~empty~ and SameValue(_cell_.[[UnregisterToken]], _unregisterToken_) is *true*, then
              1. Remove _cell_ from _finalizationRegistry_.[[Cells]].
              1. Set _removed_ to *true*.
          1. Return _removed_.