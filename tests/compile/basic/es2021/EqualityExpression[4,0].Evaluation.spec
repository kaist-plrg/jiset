        1. Let _lref_ be the result of evaluating |EqualityExpression|.
        1. Let _lval_ be ? GetValue(_lref_).
        1. Let _rref_ be the result of evaluating |RelationalExpression|.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Let _r_ be the result of performing Strict Equality Comparison _rval_ === _lval_.
        1. Assert: _r_ is a normal completion.
        1. If _r_.[[Value]] is *true*, return *false*. Otherwise, return *true*.