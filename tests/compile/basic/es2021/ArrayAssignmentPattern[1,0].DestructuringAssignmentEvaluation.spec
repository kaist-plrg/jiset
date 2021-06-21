          1. Let _iteratorRecord_ be ? GetIterator(_value_).
          1. Let _result_ be IteratorDestructuringAssignmentEvaluation of |AssignmentElementList| with argument _iteratorRecord_.
          1. If _iteratorRecord_.[[Done]] is *false*, return ? IteratorClose(_iteratorRecord_, _result_).
          1. Return _result_.