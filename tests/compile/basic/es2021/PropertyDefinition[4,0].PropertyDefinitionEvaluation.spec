          1. Let _exprValue_ be the result of evaluating |AssignmentExpression|.
          1. Let _fromValue_ be ? GetValue(_exprValue_).
          1. Let _excludedNames_ be a new empty List.
          1. Return ? CopyDataProperties(_object_, _fromValue_, _excludedNames_).