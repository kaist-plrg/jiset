        1. For each element _obj_ of _S_, do
          1. For each WeakRef _ref_ such that _ref_.[[WeakRefTarget]] is _obj_, do
            1. Set _ref_.[[WeakRefTarget]] to ~empty~.
          1. For each FinalizationRegistry _fg_ such that _fg_.[[Cells]] contains a Record _cell_ such that _cell_.[[WeakRefTarget]] is _obj_, do
            1. Set _cell_.[[WeakRefTarget]] to ~empty~.
            1. Optionally, perform ! HostEnqueueFinalizationRegistryCleanupJob(_fg_).
          1. For each WeakMap _map_ such that _map_.[[WeakMapData]] contains a Record _r_ such that _r_.[[Key]] is _obj_, do
            1. Set _r_.[[Key]] to ~empty~.
            1. Set _r_.[[Value]] to ~empty~.
          1. For each WeakSet _set_ such that _set_.[[WeakSetData]] contains _obj_, do
            1. Replace the element of _set_.[[WeakSetData]] whose value is _obj_ with an element whose value is ~empty~.