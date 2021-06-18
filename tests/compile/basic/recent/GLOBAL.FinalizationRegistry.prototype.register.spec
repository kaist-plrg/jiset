          1. Let _finalizationRegistry_ be the *this* value.
          1. Perform ? RequireInternalSlot(_finalizationRegistry_, [[Cells]]).
          1. If Type(_target_) is not Object, throw a *TypeError* exception.
          1. If SameValue(_target_, _heldValue_) is *true*, throw a *TypeError* exception.
          1. If Type(_unregisterToken_) is not Object, then
            1. If _unregisterToken_ is not *undefined*, throw a *TypeError* exception.
            1. Set _unregisterToken_ to ~empty~.
          1. Let _cell_ be the Record { [[WeakRefTarget]]: _target_, [[HeldValue]]: _heldValue_, [[UnregisterToken]]: _unregisterToken_ }.
          1. Append _cell_ to _finalizationRegistry_.[[Cells]].
          1. Return *undefined*.