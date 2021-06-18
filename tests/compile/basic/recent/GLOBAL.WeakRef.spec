          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. If Type(_target_) is not Object, throw a *TypeError* exception.
          1. Let _weakRef_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%WeakRef.prototype%"*, « [[WeakRefTarget]] »).
          1. Perform ! AddToKeptObjects(_target_).
          1. Set _weakRef_.[[WeakRefTarget]] to _target_.
          1. Return _weakRef_.