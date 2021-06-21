          1. Let _iteratorRecord_ be ? GetIterator(_value_).
          1. If |Elision| is present, then
            1. Let _status_ be IteratorDestructuringAssignmentEvaluation of |Elision| with argument _iteratorRecord_.
            1. If _status_ is an abrupt completion, then
              1. Assert: _iteratorRecord_.[[Done]] is *true*.
              1. Return Completion(_status_).
          1. Let _result_ be IteratorDestructuringAssignmentEvaluation of |AssignmentRestElement| with argument _iteratorRecord_.
          1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iteratorRecord_, _result_).
          1. Return _result_.