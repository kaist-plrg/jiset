            1. Assert: Type(_C_) is Object.
            1. If IsPromise(_x_) is *true*, then
              1. Let _xConstructor_ be ? Get(_x_, *"constructor"*).
              1. If SameValue(_xConstructor_, _C_) is *true*, return _x_.
            1. Let _promiseCapability_ be ? NewPromiseCapability(_C_).
            1. Perform ? Call(_promiseCapability_.[[Resolve]], *undefined*, « _x_ »).
            1. Return _promiseCapability_.[[Promise]].