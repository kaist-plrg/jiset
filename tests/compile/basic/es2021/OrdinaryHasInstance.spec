        1. If IsCallable(_C_) is *false*, return *false*.
        1. If _C_ has a [[BoundTargetFunction]] internal slot, then
          1. Let _BC_ be _C_.[[BoundTargetFunction]].
          1. Return ? InstanceofOperator(_O_, _BC_).
        1. If Type(_O_) is not Object, return *false*.
        1. Let _P_ be ? Get(_C_, *"prototype"*).
        1. If Type(_P_) is not Object, throw a *TypeError* exception.
        1. Repeat,
          1. Set _O_ to ? _O_.[[GetPrototypeOf]]().
          1. If _O_ is *null*, return *false*.
          1. If SameValue(_P_, _O_) is *true*, return *true*.