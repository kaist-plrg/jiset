          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. If IsCallable(_callbackfn_) is *false*, throw a *TypeError* exception.
          1. If _len_ is 0 and _initialValue_ is not present, throw a *TypeError* exception.
          1. Let _k_ be _len_ - 1.
          1. Let _accumulator_ be *undefined*.
          1. If _initialValue_ is present, then
            1. Set _accumulator_ to _initialValue_.
          1. Else,
            1. Let _kPresent_ be *false*.
            1. Repeat, while _kPresent_ is *false* and _k_ ≥ 0
              1. Let _Pk_ be ! ToString(_k_).
              1. Set _kPresent_ to ? HasProperty(_O_, _Pk_).
              1. If _kPresent_ is *true*, then
                1. Set _accumulator_ to ? Get(_O_, _Pk_).
              1. Set _k_ to _k_ - 1.
            1. If _kPresent_ is *false*, throw a *TypeError* exception.
          1. Repeat, while _k_ ≥ 0
            1. Let _Pk_ be ! ToString(_k_).
            1. Let _kPresent_ be ? HasProperty(_O_, _Pk_).
            1. If _kPresent_ is *true*, then
              1. Let _kValue_ be ? Get(_O_, _Pk_).
              1. Set _accumulator_ to ? Call(_callbackfn_, *undefined*, « _accumulator_, _kValue_, _k_, _O_ »).
            1. Set _k_ to _k_ - 1.
          1. Return _accumulator_.