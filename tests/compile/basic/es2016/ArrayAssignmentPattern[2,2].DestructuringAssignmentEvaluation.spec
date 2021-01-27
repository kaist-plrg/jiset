          1. Let _iterator_ be ? GetIterator(_value_).
          1. Let _iteratorRecord_ be Record {[[Iterator]]: _iterator_, [[Done]]: *false*}.
          1. Let _status_ be the result of performing IteratorDestructuringAssignmentEvaluation of |AssignmentElementList| using _iteratorRecord_ as the argument.
          1. If _status_ is an abrupt completion, then
            1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iterator_, _status_).
            1. Return Completion(_status_).
          1. If |Elision| is present, then
            1. Let _status_ be the result of performing IteratorDestructuringAssignmentEvaluation of |Elision| with _iteratorRecord_ as the argument.
            1. If _status_ is an abrupt completion, then
              1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iterator_, _status_).
              1. Return Completion(_status_).
          1. If |AssignmentRestElement| is present, then
            1. Let _status_ be the result of performing IteratorDestructuringAssignmentEvaluation of |AssignmentRestElement| with _iteratorRecord_ as the argument.
          1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iterator_, _status_).
          1. Return Completion(_status_).