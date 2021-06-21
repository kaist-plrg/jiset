        1. Assert: Type(_O_) is Object.
        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Assert: Type(_Throw_) is Boolean.
        1. Let _success_ be ? _O_.[[Set]](_P_, _V_, _O_).
        1. If _success_ is *false* and _Throw_ is *true*, throw a *TypeError* exception.
        1. Return _success_.