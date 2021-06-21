        1. Assert: Type(_O_) is Object.
        1. Let _C_ be ? Get(_O_, *"constructor"*).
        1. If _C_ is *undefined*, return _defaultConstructor_.
        1. If Type(_C_) is not Object, throw a *TypeError* exception.
        1. Let _S_ be ? Get(_C_, @@species).
        1. If _S_ is either *undefined* or *null*, return _defaultConstructor_.
        1. If IsConstructor(_S_) is *true*, return _S_.
        1. Throw a *TypeError* exception.