          1. Let _status_ be the result of performing IteratorDestructuringAssignmentEvaluation of |Elision| with _iteratorRecord_ as the argument.
          1. ReturnIfAbrupt(_status_).
          1. Return the result of performing IteratorBindingInitialization of |BindingElement| with _iteratorRecord_ and _environment_ as the arguments.