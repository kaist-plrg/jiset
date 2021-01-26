          1. Perform ? RequireInternalSlot(_set_, [[SetData]]).
          1. Let _iterator_ be OrdinaryObjectCreate(%SetIteratorPrototype%, « [[IteratedSet]], [[SetNextIndex]], [[SetIterationKind]] »).
          1. Set _iterator_.[[IteratedSet]] to _set_.
          1. Set _iterator_.[[SetNextIndex]] to 0.
          1. Set _iterator_.[[SetIterationKind]] to _kind_.
          1. Return _iterator_.