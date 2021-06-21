          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. If IsCallable(_cleanupCallback_) is *false*, throw a *TypeError* exception.
          1. Let _finalizationRegistry_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%FinalizationRegistry.prototype%"*, « [[Realm]], [[CleanupCallback]], [[Cells]] »).
          1. Let _fn_ be the active function object.
          1. Set _finalizationRegistry_.[[Realm]] to _fn_.[[Realm]].
          1. Set _finalizationRegistry_.[[CleanupCallback]] to _cleanupCallback_.
          1. Set _finalizationRegistry_.[[Cells]] to a new empty List.
          1. Return _finalizationRegistry_.