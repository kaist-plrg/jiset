          1. Perform ? RequireObjectCoercible(_value_).
          1. Let _excludedNames_ be the result of performing ? PropertyDestructuringAssignmentEvaluation for |AssignmentPropertyList| using _value_ as the argument.
          1. Return the result of performing RestDestructuringAssignmentEvaluation of |AssignmentRestProperty| with _value_ and _excludedNames_ as the arguments.