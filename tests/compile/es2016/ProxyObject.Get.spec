        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _handler_ be the value of the [[ProxyHandler]] internal slot of _O_.
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be the value of the [[ProxyTarget]] internal slot of _O_.
        1. Let _trap_ be ? GetMethod(_handler_, `"get"`).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[Get]](_P_, _Receiver_).
        1. Let _trapResult_ be ? Call(_trap_, _handler_, « _target_, _P_, _Receiver_ »).
        1. Let _targetDesc_ be ? _target_.[[GetOwnProperty]](_P_).
        1. If _targetDesc_ is not *undefined*, then
          1. If IsDataDescriptor(_targetDesc_) is *true* and _targetDesc_.[[Configurable]] is *false* and _targetDesc_.[[Writable]] is *false*, then
            1. If SameValue(_trapResult_, _targetDesc_.[[Value]]) is *false*, throw a *TypeError* exception.
          1. If IsAccessorDescriptor(_targetDesc_) is *true* and _targetDesc_.[[Configurable]] is *false* and _targetDesc_.[[Get]] is *undefined*, then
            1. If _trapResult_ is not *undefined*, throw a *TypeError* exception.
        1. Return _trapResult_.