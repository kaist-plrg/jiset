        1. Let _lref_ be the result of evaluating |RelationalExpression|.
        1. Let _lval_ be ? GetValue(_lref_).
        1. Let _rref_ be the result of evaluating |ShiftExpression|.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Return ? InstanceofOperator(_lval_, _rval_).