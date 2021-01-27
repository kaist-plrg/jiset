          1. If Type(_map_) is not Object, throw a *TypeError* exception.
          1. If _map_ does not have a [[MapData]] internal slot, throw a *TypeError* exception.
          1. Let _iterator_ be ObjectCreate(%MapIteratorPrototype%, « [[Map]], [[MapNextIndex]], [[MapIterationKind]] »).
          1. Set _iterator_'s [[Map]] internal slot to _map_.
          1. Set _iterator_'s [[MapNextIndex]] internal slot to 0.
          1. Set _iterator_'s [[MapIterationKind]] internal slot to _kind_.
          1. Return _iterator_.