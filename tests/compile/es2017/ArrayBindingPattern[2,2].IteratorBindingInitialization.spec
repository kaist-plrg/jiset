          1. Let _status_ be the result of performing IteratorBindingInitialization for |BindingElementList| with _iteratorRecord_ and _environment_ as arguments.
          1. ReturnIfAbrupt(_status_).
          1. Return the result of performing IteratorDestructuringAssignmentEvaluation of |Elision| with _iteratorRecord_ as the argument.