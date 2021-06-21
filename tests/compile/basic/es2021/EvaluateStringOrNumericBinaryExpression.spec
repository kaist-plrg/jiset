        1. Let _lref_ be the result of evaluating _leftOperand_.
        1. Let _lval_ be ? GetValue(_lref_).
        1. Let _rref_ be the result of evaluating _rightOperand_.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Return ? ApplyStringOrNumericBinaryOperator(_lval_, _opText_, _rval_).