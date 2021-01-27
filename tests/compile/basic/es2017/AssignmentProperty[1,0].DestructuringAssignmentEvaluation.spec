          1. Let _name_ be the result of evaluating |PropertyName|.
          1. ReturnIfAbrupt(_name_).
          1. Return the result of performing KeyedDestructuringAssignmentEvaluation of |AssignmentElement| with _value_ and _name_ as the arguments.