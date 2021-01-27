          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. If Type(_P_) is String, then
            1. Let _numericIndex_ be ! CanonicalNumericIndexString(_P_).
            1. If _numericIndex_ is not *undefined*, then
              1. Let _buffer_ be _O_.[[ViewedArrayBuffer]].
              1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
              1. If ! IsValidIntegerIndex(_O_, _numericIndex_) is *false*, return *false*.
              1. Return *true*.
          1. Return ? OrdinaryHasProperty(_O_, _P_).