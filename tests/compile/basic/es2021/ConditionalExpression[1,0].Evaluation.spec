        1. Let _lref_ be the result of evaluating |ShortCircuitExpression|.
        1. Let _lval_ be ! ToBoolean(? GetValue(_lref_)).
        1. If _lval_ is *true*, then
          1. Let _trueRef_ be the result of evaluating the first |AssignmentExpression|.
          1. Return ? GetValue(_trueRef_).
        1. Else,
          1. Let _falseRef_ be the result of evaluating the second |AssignmentExpression|.
          1. Return ? GetValue(_falseRef_).