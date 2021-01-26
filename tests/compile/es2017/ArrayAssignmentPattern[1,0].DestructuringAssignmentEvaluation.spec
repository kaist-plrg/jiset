          1. Let _iterator_ be ? GetIterator(_value_).
          1. Let _iteratorRecord_ be Record {[[Iterator]]: _iterator_, [[Done]]: *false*}.
          1. Let _result_ be the result of performing IteratorDestructuringAssignmentEvaluation of |AssignmentElementList| using _iteratorRecord_ as the argument.
          1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iterator_, _result_).
          1. Return _result_.