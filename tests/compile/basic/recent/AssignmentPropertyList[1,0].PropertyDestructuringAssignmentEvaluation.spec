          1. Let _propertyNames_ be ? PropertyDestructuringAssignmentEvaluation of |AssignmentPropertyList| with argument _value_.
          1. Let _nextNames_ be ? PropertyDestructuringAssignmentEvaluation of |AssignmentProperty| with argument _value_.
          1. Append each item in _nextNames_ to the end of _propertyNames_.
          1. Return _propertyNames_.