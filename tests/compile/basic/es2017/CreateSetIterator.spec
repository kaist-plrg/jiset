          1. If Type(_set_) is not Object, throw a *TypeError* exception.
          1. If _set_ does not have a [[SetData]] internal slot, throw a *TypeError* exception.
          1. Let _iterator_ be ObjectCreate(%SetIteratorPrototype%, « [[IteratedSet]], [[SetNextIndex]], [[SetIterationKind]] »).
          1. Set _iterator_.[[IteratedSet]] to _set_.
          1. Set _iterator_.[[SetNextIndex]] to 0.
          1. Set _iterator_.[[SetIterationKind]] to _kind_.
          1. Return _iterator_.