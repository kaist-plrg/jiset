          1. Assert: _O_ is an Integer-Indexed exotic object.
          1. If IsDetachedBuffer(_O_.[[ViewedArrayBuffer]]) is *true*, return *false*.
          1. If ! IsIntegralNumber(_index_) is *false*, return *false*.
          1. If _index_ is *-0*<sub>𝔽</sub>, return *false*.
          1. If ℝ(_index_) < 0 or ℝ(_index_) ≥ _O_.[[ArrayLength]], return *false*.
          1. Return *true*.