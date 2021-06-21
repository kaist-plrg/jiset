        1. Let _lref_ be the result of evaluating |CoalesceExpressionHead|.
        1. Let _lval_ be ? GetValue(_lref_).
        1. If _lval_ is *undefined* or *null*, then
          1. Let _rref_ be the result of evaluating |BitwiseORExpression|.
          1. Return ? GetValue(_rref_).
        1. Otherwise, return _lval_.