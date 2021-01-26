          1. Let _spreadRef_ be the result of evaluating |AssignmentExpression|.
          1. Let _spreadObj_ be ? GetValue(_spreadRef_).
          1. Let _iterator_ be ? GetIterator(_spreadObj_).
          1. Repeat
            1. Let _next_ be ? IteratorStep(_iterator_).
            1. If _next_ is *false*, return _nextIndex_.
            1. Let _nextValue_ be ? IteratorValue(_next_).
            1. Let _status_ be CreateDataProperty(_array_, ToString(ToUint32(_nextIndex_)), _nextValue_).
            1. Assert: _status_ is *true*.
            1. Let _nextIndex_ be _nextIndex_ + 1.