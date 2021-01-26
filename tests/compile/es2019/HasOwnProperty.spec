        1. Assert: Type(_O_) is Object.
        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _desc_ be ? _O_.[[GetOwnProperty]](_P_).
        1. If _desc_ is *undefined*, return *false*.
        1. Return *true*.