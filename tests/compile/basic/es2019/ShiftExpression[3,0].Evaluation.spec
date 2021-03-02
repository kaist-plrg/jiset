          1. Let _lref_ be the result of evaluating |ShiftExpression|.
          1. Let _lval_ be ? GetValue(_lref_).
          1. Let _rref_ be the result of evaluating |AdditiveExpression|.
          1. Let _rval_ be ? GetValue(_rref_).
          1. Let _lnum_ be ? ToUint32(_lval_).
          1. Let _rnum_ be ? ToUint32(_rval_).
          1. Let _shiftCount_ be the result of masking out all but the least significant 5 bits of _rnum_, that is, compute _rnum_ & 0x1F.
          1. Return the result of performing a zero-filling right shift of _lnum_ by _shiftCount_ bits. Vacated bits are filled with zero. The result is an unsigned 32-bit integer.