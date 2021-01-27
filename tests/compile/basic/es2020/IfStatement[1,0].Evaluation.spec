        1. Let _exprRef_ be the result of evaluating |Expression|.
        1. Let _exprValue_ be ! ToBoolean(? GetValue(_exprRef_)).
        1. If _exprValue_ is *false*, then
          1. Return NormalCompletion(*undefined*).
        1. Else,
          1. Let _stmtCompletion_ be the result of evaluating |Statement|.
          1. Return Completion(UpdateEmpty(_stmtCompletion_, *undefined*)).