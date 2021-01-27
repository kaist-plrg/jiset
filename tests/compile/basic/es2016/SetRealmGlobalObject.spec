        1. If _globalObj_ is *undefined*, then
          1. Let _intrinsics_ be _realmRec_.[[Intrinsics]].
          1. Let _globalObj_ be ObjectCreate(_intrinsics_.[[%ObjectPrototype%]]).
        1. Assert: Type(_globalObj_) is Object.
        1. If _thisValue_ is *undefined*, let _thisValue_ be _globalObj_.
        1. Set _realmRec_.[[GlobalObject]] to _globalObj_.
        1. Let _newGlobalEnv_ be NewGlobalEnvironment(_globalObj_, _thisValue_).
        1. Set _realmRec_.[[GlobalEnv]] to _newGlobalEnv_.
        1. Return _realmRec_.