        1. Let _handler_ be the value of the [[ProxyHandler]] internal slot of _O_.
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be the value of the [[ProxyTarget]] internal slot of _O_.
        1. Let _trap_ be ? GetMethod(_handler_, `"isExtensible"`).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[IsExtensible]]().
        1. Let _booleanTrapResult_ be ToBoolean(? Call(_trap_, _handler_, « _target_ »)).
        1. Let _targetResult_ be ? _target_.[[IsExtensible]]().
        1. If SameValue(_booleanTrapResult_, _targetResult_) is *false*, throw a *TypeError* exception.
        1. Return _booleanTrapResult_.