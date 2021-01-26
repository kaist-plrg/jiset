          1. If Type(_V_) is not Object, return *false*.
          1. Let _O_ be ? ToObject(*this* value).
          1. Repeat,
            1. Set _V_ to ? _V_.[[GetPrototypeOf]]().
            1. If _V_ is *null*, return *false*.
            1. If SameValue(_O_, _V_) is *true*, return *true*.