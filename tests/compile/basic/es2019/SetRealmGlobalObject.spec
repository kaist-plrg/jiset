        1. If _globalObj_ is *undefined*, then
          1. Let _intrinsics_ be _realmRec_.[[Intrinsics]].
          1. Set _globalObj_ to ObjectCreate(_intrinsics_.[[%ObjectPrototype%]]).
        1. Assert: Type(_globalObj_) is Object.
        1. If _thisValue_ is *undefined*, set _thisValue_ to _globalObj_.
        1. Set _realmRec_.[[GlobalObject]] to _globalObj_.
        1. Let _newGlobalEnv_ be NewGlobalEnvironment(_globalObj_, _thisValue_).
        1. Set _realmRec_.[[GlobalEnv]] to _newGlobalEnv_.
        1. Return _realmRec_.