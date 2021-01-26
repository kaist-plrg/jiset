          1. Let _propKey_ be the result of evaluating |PropertyName|.
          1. ReturnIfAbrupt(_propKey_).
          1. Let _exprValueRef_ be the result of evaluating |AssignmentExpression|.
          1. Let _propValue_ be ? GetValue(_exprValueRef_).
          1. If IsAnonymousFunctionDefinition(|AssignmentExpression|) is *true*, then
            1. Let _hasNameProperty_ be ? HasOwnProperty(_propValue_, `"name"`).
            1. If _hasNameProperty_ is *false*, perform SetFunctionName(_propValue_, _propKey_).
          1. Assert: _enumerable_ is *true*.
          1. Return CreateDataPropertyOrThrow(_object_, _propKey_, _propValue_).