        1. Assert: _obj_ is a callable object.
        1. If _obj_ has a [[Realm]] internal slot, then
          1. Return _obj_.[[Realm]].
        1. If _obj_ is a Bound Function exotic object, then
          1. Let _target_ be _obj_.[[BoundTargetFunction]].
          1. Return ? GetFunctionRealm(_target_).
        1. If _obj_ is a Proxy exotic object, then
          1. If _obj_.[[ProxyHandler]] is *null*, throw a *TypeError* exception.
          1. Let _proxyTarget_ be _obj_.[[ProxyTarget]].
          1. Return ? GetFunctionRealm(_proxyTarget_).
        1. Return the current Realm Record.