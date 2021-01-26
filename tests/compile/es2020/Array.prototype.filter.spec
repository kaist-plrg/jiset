          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. If IsCallable(_callbackfn_) is *false*, throw a *TypeError* exception.
          1. Let _A_ be ? ArraySpeciesCreate(_O_, 0).
          1. Let _k_ be 0.
          1. Let _to_ be 0.
          1. Repeat, while _k_ < _len_
            1. Let _Pk_ be ! ToString(_k_).
            1. Let _kPresent_ be ? HasProperty(_O_, _Pk_).
            1. If _kPresent_ is *true*, then
              1. Let _kValue_ be ? Get(_O_, _Pk_).
              1. Let _selected_ be ! ToBoolean(? Call(_callbackfn_, _thisArg_, « _kValue_, _k_, _O_ »)).
              1. If _selected_ is *true*, then
                1. Perform ? CreateDataPropertyOrThrow(_A_, ! ToString(_to_), _kValue_).
                1. Set _to_ to _to_ + 1.
            1. Set _k_ to _k_ + 1.
          1. Return _A_.