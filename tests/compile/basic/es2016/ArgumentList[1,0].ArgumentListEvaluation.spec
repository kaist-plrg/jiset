          1. Let _list_ be a new empty List.
          1. Let _spreadRef_ be the result of evaluating |AssignmentExpression|.
          1. Let _spreadObj_ be ? GetValue(_spreadRef_).
          1. Let _iterator_ be ? GetIterator(_spreadObj_).
          1. Repeat
            1. Let _next_ be ? IteratorStep(_iterator_).
            1. If _next_ is *false*, return _list_.
            1. Let _nextArg_ be ? IteratorValue(_next_).
            1. Append _nextArg_ as the last element of _list_.