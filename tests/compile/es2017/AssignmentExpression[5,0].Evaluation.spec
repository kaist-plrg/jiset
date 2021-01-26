        1. Let _lref_ be the result of evaluating |LeftHandSideExpression|.
        1. Let _lval_ be ? GetValue(_lref_).
        1. Let _rref_ be the result of evaluating |AssignmentExpression|.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Let _op_ be the `@` where |AssignmentOperator| is `@=`.
        1. Let _r_ be the result of applying _op_ to _lval_ and _rval_ as if evaluating the expression _lval_ _op_ _rval_.
        1. Perform ? PutValue(_lref_, _r_).
        1. Return _r_.