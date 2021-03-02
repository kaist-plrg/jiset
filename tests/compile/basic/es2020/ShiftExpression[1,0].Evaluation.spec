          1. Let _lref_ be the result of evaluating |ShiftExpression|.
          1. Let _lval_ be ? GetValue(_lref_).
          1. Let _rref_ be the result of evaluating |AdditiveExpression|.
          1. Let _rval_ be ? GetValue(_rref_).
          1. Let _lnum_ be ? ToNumeric(_lval_).
          1. Let _rnum_ be ? ToNumeric(_rval_).
          1. If Type(_lnum_) is different from Type(_rnum_), throw a *TypeError* exception.
          1. Let _T_ be Type(_lnum_).
          1. Return _T_::leftShift(_lnum_, _rnum_).