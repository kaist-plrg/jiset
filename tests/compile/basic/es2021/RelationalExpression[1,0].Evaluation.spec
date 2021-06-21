        1. Let _lref_ be the result of evaluating |RelationalExpression|.
        1. Let _lval_ be ? GetValue(_lref_).
        1. Let _rref_ be the result of evaluating |ShiftExpression|.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Let _r_ be the result of performing Abstract Relational Comparison _lval_ < _rval_.
        1. ReturnIfAbrupt(_r_).
        1. If _r_ is *undefined*, return *false*. Otherwise, return _r_.