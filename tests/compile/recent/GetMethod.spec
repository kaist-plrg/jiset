        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _func_ be ? GetV(_V_, _P_).
        1. If _func_ is either *undefined* or *null*, return *undefined*.
        1. If IsCallable(_func_) is *false*, throw a *TypeError* exception.
        1. Return _func_.