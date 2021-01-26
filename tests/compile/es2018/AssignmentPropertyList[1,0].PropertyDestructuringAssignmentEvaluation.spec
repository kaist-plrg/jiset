          1. Let _propertyNames_ be the result of performing ? PropertyDestructuringAssignmentEvaluation for |AssignmentPropertyList| using _value_ as the argument.
          1. Let _nextNames_ be the result of performing ? PropertyDestructuringAssignmentEvaluation for |AssignmentProperty| using _value_ as the argument.
          1. Append each item in _nextNames_ to the end of _propertyNames_.
          1. Return _propertyNames_.