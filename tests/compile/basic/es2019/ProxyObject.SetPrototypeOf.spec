        1. Assert: Either Type(_V_) is Object or Type(_V_) is Null.
        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, `"setPrototypeOf"`).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[SetPrototypeOf]](_V_).
        1. Let _booleanTrapResult_ be ToBoolean(? Call(_trap_, _handler_, « _target_, _V_ »)).
        1. If _booleanTrapResult_ is *false*, return *false*.
        1. Let _extensibleTarget_ be ? IsExtensible(_target_).
        1. If _extensibleTarget_ is *true*, return *true*.
        1. Let _targetProto_ be ? _target_.[[GetPrototypeOf]]().
        1. If SameValue(_V_, _targetProto_) is *false*, throw a *TypeError* exception.
        1. Return *true*.