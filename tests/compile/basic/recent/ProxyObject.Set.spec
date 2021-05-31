        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, *"set"*).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[Set]](_P_, _V_, _Receiver_).
        1. Let _booleanTrapResult_ be ! ToBoolean(? Call(_trap_, _handler_, « _target_, _P_, _V_, _Receiver_ »)).
        1. If _booleanTrapResult_ is *false*, return *false*.
        1. Let _targetDesc_ be ? _target_.[[GetOwnProperty]](_P_).
        1. If _targetDesc_ is not *undefined* and _targetDesc_.[[Configurable]] is *false*, then
          1. If IsDataDescriptor(_targetDesc_) is *true* and _targetDesc_.[[Writable]] is *false*, then
            1. If SameValue(_V_, _targetDesc_.[[Value]]) is *false*, throw a *TypeError* exception.
          1. If IsAccessorDescriptor(_targetDesc_) is *true*, then
            1. If _targetDesc_.[[Set]] is *undefined*, throw a *TypeError* exception.
        1. Return *true*.