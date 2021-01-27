        1. Assert: _obj_ is a callable object.
        1. If _obj_ has a [[Realm]] internal slot, then
          1. Return _obj_'s [[Realm]] internal slot.
        1. If _obj_ is a Bound Function exotic object, then
          1. Let _target_ be _obj_'s [[BoundTargetFunction]] internal slot.
          1. Return ? GetFunctionRealm(_target_).
        1. If _obj_ is a Proxy exotic object, then
          1. If the value of the [[ProxyHandler]] internal slot of _obj_ is *null*, throw a *TypeError* exception.
          1. Let _proxyTarget_ be the value of _obj_'s [[ProxyTarget]] internal slot.
          1. Return ? GetFunctionRealm(_proxyTarget_).
        1. Return the current Realm Record.