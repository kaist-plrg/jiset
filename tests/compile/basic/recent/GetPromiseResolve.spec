            1. Assert: IsConstructor(_promiseConstructor_) is *true*.
            1. Let _promiseResolve_ be ? Get(_promiseConstructor_, *"resolve"*).
            1. If IsCallable(_promiseResolve_) is *false*, throw a *TypeError* exception.
            1. Return _promiseResolve_.