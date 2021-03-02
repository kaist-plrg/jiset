        1. Let _lref_ be the result of evaluating |LogicalANDExpression|.
        1. Let _lval_ be ? GetValue(_lref_).
        1. Let _lbool_ be ToBoolean(_lval_).
        1. If _lbool_ is *false*, return _lval_.
        1. Let _rref_ be the result of evaluating |BitwiseORExpression|.
        1. Return ? GetValue(_rref_).