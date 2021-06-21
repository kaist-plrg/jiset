          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Let _map_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%WeakMap.prototype%"*, « [[WeakMapData]] »).
          1. Set _map_.[[WeakMapData]] to a new empty List.
          1. If _iterable_ is either *undefined* or *null*, return _map_.
          1. Let _adder_ be ? Get(_map_, *"set"*).
          1. Return ? AddEntriesFromIterable(_map_, _iterable_, _adder_).