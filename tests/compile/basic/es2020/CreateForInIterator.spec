            1. Assert: Type(_object_) is Object.
            1. Let _iterator_ be OrdinaryObjectCreate(%ForInIteratorPrototype%, « [[Object]], [[ObjectWasVisited]], [[VisitedKeys]], [[RemainingKeys]] »).
            1. Set _iterator_.[[Object]] to _object_.
            1. Set _iterator_.[[ObjectWasVisited]] to *false*.
            1. Set _iterator_.[[VisitedKeys]] to a new empty List.
            1. Set _iterator_.[[RemainingKeys]] to a new empty List.
            1. Return _iterator_.