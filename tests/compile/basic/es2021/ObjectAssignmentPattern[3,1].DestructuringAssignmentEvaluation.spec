          1. Perform ? RequireObjectCoercible(_value_).
          1. Let _excludedNames_ be ? PropertyDestructuringAssignmentEvaluation of |AssignmentPropertyList| with argument _value_.
          1. Return the result of performing RestDestructuringAssignmentEvaluation of |AssignmentRestProperty| with arguments _value_ and _excludedNames_.