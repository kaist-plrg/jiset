          1. If |Elision| is present, then
            1. Perform ? IteratorDestructuringAssignmentEvaluation of |Elision| with _iteratorRecord_ as the argument.
          1. Return the result of performing IteratorBindingInitialization for |BindingRestElement| with _iteratorRecord_ and _environment_ as arguments.