          1. Perform ? RequireInternalSlot(_map_, [[MapData]]).
          1. Let _iterator_ be OrdinaryObjectCreate(%MapIteratorPrototype%, « [[IteratedMap]], [[MapNextIndex]], [[MapIterationKind]] »).
          1. Set _iterator_.[[IteratedMap]] to _map_.
          1. Set _iterator_.[[MapNextIndex]] to 0.
          1. Set _iterator_.[[MapIterationKind]] to _kind_.
          1. Return _iterator_.