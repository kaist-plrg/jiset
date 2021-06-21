          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. If IsCallable(_predicate_) is *false*, throw a *TypeError* exception.
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_,
            1. Let _Pk_ be ! ToString(𝔽(_k_)).
            1. Let _kValue_ be ? Get(_O_, _Pk_).
            1. Let _testResult_ be ! ToBoolean(? Call(_predicate_, _thisArg_, « _kValue_, 𝔽(_k_), _O_ »)).
            1. If _testResult_ is *true*, return _kValue_.
            1. Set _k_ to _k_ + 1.
          1. Return *undefined*.