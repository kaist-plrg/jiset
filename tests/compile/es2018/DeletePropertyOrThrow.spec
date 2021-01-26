        1. Assert: Type(_O_) is Object.
        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _success_ be ? _O_.[[Delete]](_P_).
        1. If _success_ is *false*, throw a *TypeError* exception.
        1. Return _success_.