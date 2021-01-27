        1. Let _left_ be the result of evaluating |MultiplicativeExpression|.
        1. Let _leftValue_ be ? GetValue(_left_).
        1. Let _right_ be the result of evaluating |ExponentiationExpression|.
        1. Let _rightValue_ be ? GetValue(_right_).
        1. Let _lnum_ be ? ToNumeric(_leftValue_).
        1. Let _rnum_ be ? ToNumeric(_rightValue_).
        1. If Type(_lnum_) is different from Type(_rnum_), throw a *TypeError* exception.
        1. Let _T_ be Type(_lnum_).
        1. If |MultiplicativeOperator| is `*`, return _T_::multiply(_lnum_, _rnum_).
        1. If |MultiplicativeOperator| is `/`, return _T_::divide(_lnum_, _rnum_).
        1. Else,
          1. Assert: |MultiplicativeOperator| is `%`.
          1. Return _T_::remainder(_lnum_, _rnum_).