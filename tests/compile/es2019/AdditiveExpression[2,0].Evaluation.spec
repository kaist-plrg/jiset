          1. Let _lref_ be the result of evaluating |AdditiveExpression|.
          1. Let _lval_ be ? GetValue(_lref_).
          1. Let _rref_ be the result of evaluating |MultiplicativeExpression|.
          1. Let _rval_ be ? GetValue(_rref_).
          1. Let _lnum_ be ? ToNumber(_lval_).
          1. Let _rnum_ be ? ToNumber(_rval_).
          1. Return the result of applying the subtraction operation to _lnum_ and _rnum_. See the note below <emu-xref href="#sec-applying-the-additive-operators-to-numbers"></emu-xref>.