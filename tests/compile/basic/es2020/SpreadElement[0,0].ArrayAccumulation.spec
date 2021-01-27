          1. Let _spreadRef_ be the result of evaluating |AssignmentExpression|.
          1. Let _spreadObj_ be ? GetValue(_spreadRef_).
          1. Let _iteratorRecord_ be ? GetIterator(_spreadObj_).
          1. Repeat,
            1. Let _next_ be ? IteratorStep(_iteratorRecord_).
            1. If _next_ is *false*, return _nextIndex_.
            1. Let _nextValue_ be ? IteratorValue(_next_).
            1. Perform ! CreateDataPropertyOrThrow(_array_, ! ToString(_nextIndex_), _nextValue_).
            1. Set _nextIndex_ to _nextIndex_ + 1.