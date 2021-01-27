          1. If Type(_P_) is Symbol, return OrdinaryHasProperty(_O_, _P_).
          1. Let _exports_ be the value of _O_'s [[Exports]] internal slot.
          1. If _P_ is an element of _exports_, return *true*.
          1. Return *false*.