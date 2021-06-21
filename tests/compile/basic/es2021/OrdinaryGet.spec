          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _desc_ be ? _O_.[[GetOwnProperty]](_P_).
          1. If _desc_ is *undefined*, then
            1. Let _parent_ be ? _O_.[[GetPrototypeOf]]().
            1. If _parent_ is *null*, return *undefined*.
            1. Return ? _parent_.[[Get]](_P_, _Receiver_).
          1. If IsDataDescriptor(_desc_) is *true*, return _desc_.[[Value]].
          1. Assert: IsAccessorDescriptor(_desc_) is *true*.
          1. Let _getter_ be _desc_.[[Get]].
          1. If _getter_ is *undefined*, return *undefined*.
          1. Return ? Call(_getter_, _Receiver_).