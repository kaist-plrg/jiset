          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _exports_ be the value of _O_'s [[Exports]] internal slot.
          1. If _P_ is an element of _exports_, return *false*.
          1. Return *true*.