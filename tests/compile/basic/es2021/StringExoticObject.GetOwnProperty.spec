          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _desc_ be OrdinaryGetOwnProperty(_S_, _P_).
          1. If _desc_ is not *undefined*, return _desc_.
          1. Return ! StringGetOwnProperty(_S_, _P_).