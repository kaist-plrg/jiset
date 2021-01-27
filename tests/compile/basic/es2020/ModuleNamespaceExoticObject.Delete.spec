          1. Assert: IsPropertyKey(_P_) is *true*.
          1. If Type(_P_) is Symbol, then
            1. Return ? OrdinaryDelete(_O_, _P_).
          1. Let _exports_ be _O_.[[Exports]].
          1. If _P_ is an element of _exports_, return *false*.
          1. Return *true*.