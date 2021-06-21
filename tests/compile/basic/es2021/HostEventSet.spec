        1. Let _events_ be an empty Set.
        1. For each event _E_ of EventSet(_execution_), do
          1. If _E_ is not in SharedDataBlockEventSet(_execution_), add _E_ to _events_.
        1. Return _events_.