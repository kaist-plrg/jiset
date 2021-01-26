          1. Let _lref_ be the result of evaluating |AdditiveExpression|.
          1. Let _lval_ be ? GetValue(_lref_).
          1. Let _rref_ be the result of evaluating |MultiplicativeExpression|.
          1. Let _rval_ be ? GetValue(_rref_).
          1. Let _lprim_ be ? ToPrimitive(_lval_).
          1. Let _rprim_ be ? ToPrimitive(_rval_).
          1. If Type(_lprim_) is String or Type(_rprim_) is String, then
            1. Let _lstr_ be ? ToString(_lprim_).
            1. Let _rstr_ be ? ToString(_rprim_).
            1. Return the string-concatenation of _lstr_ and _rstr_.
          1. Let _lnum_ be ? ToNumeric(_lprim_).
          1. Let _rnum_ be ? ToNumeric(_rprim_).
          1. If Type(_lnum_) is different from Type(_rnum_), throw a *TypeError* exception.
          1. Let _T_ be Type(_lnum_).
          1. Return _T_::add(_lnum_, _rnum_).