          1. If Type(_P_) is Symbol, return OrdinaryDefineOwnProperty(_O_, _P_, _Desc_).
          1. Let _current_ be ? _O_.[[GetOwnProperty]](_P_).
          1. If _current_ is *undefined*, return *false*.
          1. If _Desc_.[[Configurable]] is present and has value *true*, return *false*.
          1. If _Desc_.[[Enumerable]] is present and has value *false*, return *false*.
          1. If ! IsAccessorDescriptor(_Desc_) is *true*, return *false*.
          1. If _Desc_.[[Writable]] is present and has value *false*, return *false*.
          1. If _Desc_.[[Value]] is present, return SameValue(_Desc_.[[Value]], _current_.[[Value]]).
          1. Return *true*.