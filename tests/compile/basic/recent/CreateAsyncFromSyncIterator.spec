          1. Let _asyncIterator_ be ! OrdinaryObjectCreate(%AsyncFromSyncIteratorPrototype%, « [[SyncIteratorRecord]] »).
          1. Set _asyncIterator_.[[SyncIteratorRecord]] to _syncIteratorRecord_.
          1. Let _nextMethod_ be ! Get(_asyncIterator_, *"next"*).
          1. Let _iteratorRecord_ be the Record { [[Iterator]]: _asyncIterator_, [[NextMethod]]: _nextMethod_, [[Done]]: *false* }.
          1. Return _iteratorRecord_.