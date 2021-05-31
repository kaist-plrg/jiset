          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. If Type(_P_) is String, then
            1. Let _numericIndex_ be ! CanonicalNumericIndexString(_P_).
            1. If _numericIndex_ is not *undefined*, then
              1. If ! IsValidIntegerIndex(_O_, _numericIndex_) is *false*, return *true*; else return *false*.
          1. Return ? OrdinaryDelete(_O_, _P_).