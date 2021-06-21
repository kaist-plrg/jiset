          1. Let _propName_ be StringValue of |IdentifierReference|.
          1. Let _exprValue_ be the result of evaluating |IdentifierReference|.
          1. Let _propValue_ be ? GetValue(_exprValue_).
          1. Assert: _enumerable_ is *true*.
          1. Assert: _object_ is an ordinary, extensible object with no non-configurable properties.
          1. Return ! CreateDataPropertyOrThrow(_object_, _propName_, _propValue_).