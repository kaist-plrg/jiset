          1. Assert: _length_ is an integer Number ≥ 0.
          1. If _length_ is *-0*, let _length_ be *+0*.
          1. Let _C_ be *undefined*.
          1. Let _isArray_ be ? IsArray(_originalArray_).
          1. If _isArray_ is *true*, then
            1. Let _C_ be ? Get(_originalArray_, `"constructor"`).
            1. If IsConstructor(_C_) is *true*, then
              1. Let _thisRealm_ be the current Realm Record.
              1. Let _realmC_ be ? GetFunctionRealm(_C_).
              1. If _thisRealm_ and _realmC_ are not the same Realm Record, then
                1. If SameValue(_C_, _realmC_.[[Intrinsics]].[[%Array%]]) is *true*, let _C_ be *undefined*.
            1. If Type(_C_) is Object, then
              1. Let _C_ be ? Get(_C_, @@species).
              1. If _C_ is *null*, let _C_ be *undefined*.
          1. If _C_ is *undefined*, return ? ArrayCreate(_length_).
          1. If IsConstructor(_C_) is *false*, throw a *TypeError* exception.
          1. Return ? Construct(_C_, « _length_ »).