          1. Let _asyncIterator_ be ! ObjectCreate(%AsyncFromSyncIteratorPrototype%, « [[SyncIteratorRecord]] »).
          1. Set _asyncIterator_.[[SyncIteratorRecord]] to _syncIteratorRecord_.
          1. Return ? GetIterator(_asyncIterator_, ~async~).