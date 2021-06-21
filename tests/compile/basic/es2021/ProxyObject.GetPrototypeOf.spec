        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, *"getPrototypeOf"*).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[GetPrototypeOf]]().
        1. Let _handlerProto_ be ? Call(_trap_, _handler_, « _target_ »).
        1. If Type(_handlerProto_) is neither Object nor Null, throw a *TypeError* exception.
        1. Let _extensibleTarget_ be ? IsExtensible(_target_).
        1. If _extensibleTarget_ is *true*, return _handlerProto_.
        1. Let _targetProto_ be ? _target_.[[GetPrototypeOf]]().
        1. If SameValue(_handlerProto_, _targetProto_) is *false*, throw a *TypeError* exception.
        1. Return _handlerProto_.