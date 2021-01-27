        1. If _hint_ is not present, set _hint_ to ~sync~.
        1. Assert: _hint_ is either ~sync~ or ~async~.
        1. If _method_ is not present, then
          1. If _hint_ is ~async~, then
            1. Set _method_ to ? GetMethod(_obj_, @@asyncIterator).
            1. If _method_ is *undefined*, then
              1. Let _syncMethod_ be ? GetMethod(_obj_, @@iterator).
              1. Let _syncIteratorRecord_ be ? GetIterator(_obj_, ~sync~, _syncMethod_).
              1. Return ? CreateAsyncFromSyncIterator(_syncIteratorRecord_).
          1. Otherwise, set _method_ to ? GetMethod(_obj_, @@iterator).
        1. Let _iterator_ be ? Call(_method_, _obj_).
        1. If Type(_iterator_) is not Object, throw a *TypeError* exception.
        1. Let _nextMethod_ be ? GetV(_iterator_, `"next"`).
        1. Let _iteratorRecord_ be Record { [[Iterator]]: _iterator_, [[NextMethod]]: _nextMethod_, [[Done]]: *false* }.
        1. Return _iteratorRecord_.