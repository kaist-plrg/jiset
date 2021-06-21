          1. Let _name_ be the result of evaluating |PropertyName|.
          1. ReturnIfAbrupt(_name_).
          1. Perform ? KeyedDestructuringAssignmentEvaluation of |AssignmentElement| with _value_ and _name_ as the arguments.
          1. Return a List whose sole element is _name_.