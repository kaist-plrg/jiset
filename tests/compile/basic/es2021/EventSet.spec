        1. Let _events_ be an empty Set.
        1. For each Agent Events Record _aer_ of _execution_.[[EventsRecords]], do
          1. For each event _E_ of _aer_.[[EventList]], do
            1. Add _E_ to _events_.
        1. Return _events_.