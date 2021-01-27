        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, *"getOwnPropertyDescriptor"*).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[GetOwnProperty]](_P_).
        1. Let _trapResultObj_ be ? Call(_trap_, _handler_, « _target_, _P_ »).
        1. If Type(_trapResultObj_) is neither Object nor Undefined, throw a *TypeError* exception.
        1. Let _targetDesc_ be ? _target_.[[GetOwnProperty]](_P_).
        1. If _trapResultObj_ is *undefined*, then
          1. If _targetDesc_ is *undefined*, return *undefined*.
          1. If _targetDesc_.[[Configurable]] is *false*, throw a *TypeError* exception.
          1. Let _extensibleTarget_ be ? IsExtensible(_target_).
          1. If _extensibleTarget_ is *false*, throw a *TypeError* exception.
          1. Return *undefined*.
        1. Let _extensibleTarget_ be ? IsExtensible(_target_).
        1. Let _resultDesc_ be ? ToPropertyDescriptor(_trapResultObj_).
        1. Call CompletePropertyDescriptor(_resultDesc_).
        1. Let _valid_ be IsCompatiblePropertyDescriptor(_extensibleTarget_, _resultDesc_, _targetDesc_).
        1. If _valid_ is *false*, throw a *TypeError* exception.
        1. If _resultDesc_.[[Configurable]] is *false*, then
          1. If _targetDesc_ is *undefined* or _targetDesc_.[[Configurable]] is *true*, then
            1. Throw a *TypeError* exception.
          1. If _resultDesc_ has a [[Writable]] field and _resultDesc_.[[Writable]] is *false*, then
            1. If _targetDesc_.[[Writable]] is *true*, throw a *TypeError* exception.
        1. Return _resultDesc_.