        1. Let _exprRef_ be the result of evaluating |UnaryExpression|.
        1. Let _value_ be ? GetValue(_exprRef_).
        1. Return ? AsyncFunctionAwait(_value_).