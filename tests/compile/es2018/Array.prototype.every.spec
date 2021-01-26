          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If IsCallable(_callbackfn_) is *false*, throw a *TypeError* exception.
          1. If _thisArg_ is present, let _T_ be _thisArg_; else let _T_ be *undefined*.
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_
            1. Let _Pk_ be ! ToString(_k_).
            1. Let _kPresent_ be ? HasProperty(_O_, _Pk_).
            1. If _kPresent_ is *true*, then
              1. Let _kValue_ be ? Get(_O_, _Pk_).
              1. Let _testResult_ be ToBoolean(? Call(_callbackfn_, _T_, « _kValue_, _k_, _O_ »)).
              1. If _testResult_ is *false*, return *false*.
            1. Increase _k_ by 1.
          1. Return *true*.