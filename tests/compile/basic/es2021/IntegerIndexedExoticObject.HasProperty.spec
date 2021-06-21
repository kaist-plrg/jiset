          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. If Type(_P_) is String, then
            1. Let _numericIndex_ be ! CanonicalNumericIndexString(_P_).
            1. If _numericIndex_ is not *undefined*, return ! IsValidIntegerIndex(_O_, _numericIndex_).
          1. Return ? OrdinaryHasProperty(_O_, _P_).