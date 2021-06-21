          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _desc_ be ? _O_.[[GetOwnProperty]](_P_).
          1. If _desc_ is *undefined*, return *true*.
          1. If _desc_.[[Configurable]] is *true*, then
            1. Remove the own property with name _P_ from _O_.
            1. Return *true*.
          1. Return *false*.