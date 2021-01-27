          1. Let _lref_ be the result of evaluating |DestructuringAssignmentTarget|.
          1. ReturnIfAbrupt(_lref_).
          1. Let _restObj_ be ObjectCreate(%ObjectPrototype%).
          1. Perform ? CopyDataProperties(_restObj_, _value_, _excludedNames_).
          1. Return PutValue(_lref_, _restObj_).