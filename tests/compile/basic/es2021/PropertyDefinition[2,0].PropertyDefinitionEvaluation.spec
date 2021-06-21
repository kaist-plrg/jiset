          1. Let _propKey_ be the result of evaluating |PropertyName|.
          1. ReturnIfAbrupt(_propKey_).
          1. If IsAnonymousFunctionDefinition(|AssignmentExpression|) is *true*, then
            1. Let _propValue_ be ? NamedEvaluation of |AssignmentExpression| with argument _propKey_.
          1. Else,
            1. Let _exprValueRef_ be the result of evaluating |AssignmentExpression|.
            1. Let _propValue_ be ? GetValue(_exprValueRef_).
          1. Assert: _enumerable_ is *true*.
          1. Assert: _object_ is an ordinary, extensible object with no non-configurable properties.
          1. Return ! CreateDataPropertyOrThrow(_object_, _propKey_, _propValue_).