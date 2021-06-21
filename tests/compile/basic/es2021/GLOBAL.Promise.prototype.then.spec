          1. Let _promise_ be the *this* value.
          1. If IsPromise(_promise_) is *false*, throw a *TypeError* exception.
          1. Let _C_ be ? SpeciesConstructor(_promise_, %Promise%).
          1. Let _resultCapability_ be ? NewPromiseCapability(_C_).
          1. Return PerformPromiseThen(_promise_, _onFulfilled_, _onRejected_, _resultCapability_).