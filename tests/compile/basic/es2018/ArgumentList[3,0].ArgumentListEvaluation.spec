          1. Let _precedingArgs_ be ArgumentListEvaluation of |ArgumentList|.
          1. ReturnIfAbrupt(_precedingArgs_).
          1. Let _spreadRef_ be the result of evaluating |AssignmentExpression|.
          1. Let _iteratorRecord_ be ? GetIterator(? GetValue(_spreadRef_)).
          1. Repeat,
            1. Let _next_ be ? IteratorStep(_iteratorRecord_).
            1. If _next_ is *false*, return _precedingArgs_.
            1. Let _nextArg_ be ? IteratorValue(_next_).
            1. Append _nextArg_ as the last element of _precedingArgs_.