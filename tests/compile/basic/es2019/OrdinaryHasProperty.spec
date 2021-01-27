          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _hasOwn_ be ? _O_.[[GetOwnProperty]](_P_).
          1. If _hasOwn_ is not *undefined*, return *true*.
          1. Let _parent_ be ? _O_.[[GetPrototypeOf]]().
          1. If _parent_ is not *null*, then
            1. Return ? _parent_.[[HasProperty]](_P_).
          1. Return *false*.