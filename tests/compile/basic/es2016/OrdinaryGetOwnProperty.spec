          1. Assert: IsPropertyKey(_P_) is *true*.
          1. If _O_ does not have an own property with key _P_, return *undefined*.
          1. Let _D_ be a newly created Property Descriptor with no fields.
          1. Let _X_ be _O_'s own property whose key is _P_.
          1. If _X_ is a data property, then
            1. Set _D_.[[Value]] to the value of _X_'s [[Value]] attribute.
            1. Set _D_.[[Writable]] to the value of _X_'s [[Writable]] attribute.
          1. Else _X_ is an accessor property, so
            1. Set _D_.[[Get]] to the value of _X_'s [[Get]] attribute.
            1. Set _D_.[[Set]] to the value of _X_'s [[Set]] attribute.
          1. Set _D_.[[Enumerable]] to the value of _X_'s [[Enumerable]] attribute.
          1. Set _D_.[[Configurable]] to the value of _X_'s [[Configurable]] attribute.
          1. Return _D_.