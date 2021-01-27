          1. Assert: IsPropertyKey(_P_) is *true*.
          1. If Type(_P_) is String, then
            1. Let _numericIndex_ be ! CanonicalNumericIndexString(_P_).
            1. If _numericIndex_ is not *undefined*, then
              1. Perform ? IntegerIndexedElementSet(_O_, _numericIndex_, _V_).
              1. Return *true*.
          1. Return ? OrdinarySet(_O_, _P_, _V_, _Receiver_).