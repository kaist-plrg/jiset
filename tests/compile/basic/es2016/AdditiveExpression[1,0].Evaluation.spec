          1. Let _lref_ be the result of evaluating |AdditiveExpression|.
          1. Let _lval_ be ? GetValue(_lref_).
          1. Let _rref_ be the result of evaluating |MultiplicativeExpression|.
          1. Let _rval_ be ? GetValue(_rref_).
          1. Let _lprim_ be ? ToPrimitive(_lval_).
          1. Let _rprim_ be ? ToPrimitive(_rval_).
          1. If Type(_lprim_) is String or Type(_rprim_) is String, then
            1. Let _lstr_ be ? ToString(_lprim_).
            1. Let _rstr_ be ? ToString(_rprim_).
            1. Return the String that is the result of concatenating _lstr_ and _rstr_.
          1. Let _lnum_ be ? ToNumber(_lprim_).
          1. Let _rnum_ be ? ToNumber(_rprim_).
          1. Return the result of applying the addition operation to _lnum_ and _rnum_. See the Note below <emu-xref href="#sec-applying-the-additive-operators-to-numbers"></emu-xref>.