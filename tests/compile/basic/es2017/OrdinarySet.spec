          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _ownDesc_ be ? _O_.[[GetOwnProperty]](_P_).
          1. If _ownDesc_ is *undefined*, then
            1. Let _parent_ be ? _O_.[[GetPrototypeOf]]().
            1. If _parent_ is not *null*, then
              1. Return ? _parent_.[[Set]](_P_, _V_, _Receiver_).
            1. Else,
              1. Set _ownDesc_ to the PropertyDescriptor{[[Value]]: *undefined*, [[Writable]]: *true*, [[Enumerable]]: *true*, [[Configurable]]: *true*}.
          1. If IsDataDescriptor(_ownDesc_) is *true*, then
            1. If _ownDesc_.[[Writable]] is *false*, return *false*.
            1. If Type(_Receiver_) is not Object, return *false*.
            1. Let _existingDescriptor_ be ? _Receiver_.[[GetOwnProperty]](_P_).
            1. If _existingDescriptor_ is not *undefined*, then
              1. If IsAccessorDescriptor(_existingDescriptor_) is *true*, return *false*.
              1. If _existingDescriptor_.[[Writable]] is *false*, return *false*.
              1. Let _valueDesc_ be the PropertyDescriptor{[[Value]]: _V_}.
              1. Return ? _Receiver_.[[DefineOwnProperty]](_P_, _valueDesc_).
            1. Else _Receiver_ does not currently have a property _P_,
              1. Return ? CreateDataProperty(_Receiver_, _P_, _V_).
          1. Assert: IsAccessorDescriptor(_ownDesc_) is *true*.
          1. Let _setter_ be _ownDesc_.[[Set]].
          1. If _setter_ is *undefined*, return *false*.
          1. Perform ? Call(_setter_, _Receiver_, « _V_ »).
          1. Return *true*.