            1. Let _alreadyCalled_ be the value of _F_'s [[AlreadyCalled]] internal slot.
            1. If _alreadyCalled_.[[Value]] is *true*, return *undefined*.
            1. Set _alreadyCalled_.[[Value]] to *true*.
            1. Let _index_ be the value of _F_'s [[Index]] internal slot.
            1. Let _values_ be the value of _F_'s [[Values]] internal slot.
            1. Let _promiseCapability_ be the value of _F_'s [[Capabilities]] internal slot.
            1. Let _remainingElementsCount_ be the value of _F_'s [[RemainingElements]] internal slot.
            1. Set _values_[_index_] to _x_.
            1. Set _remainingElementsCount_.[[Value]] to _remainingElementsCount_.[[Value]] - 1.
            1. If _remainingElementsCount_.[[Value]] is 0, then
              1. Let _valuesArray_ be CreateArrayFromList(_values_).
              1. Return ? Call(_promiseCapability_.[[Resolve]], *undefined*, « _valuesArray_ »).
            1. Return *undefined*.