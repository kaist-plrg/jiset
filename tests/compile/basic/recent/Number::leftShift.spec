            1. Let _lnum_ be ! ToInt32(_x_).
            1. Let _rnum_ be ! ToUint32(_y_).
            1. Let _shiftCount_ be ℝ(_rnum_) modulo 32.
            1. Return the Number value for the result of left shifting _lnum_ by _shiftCount_ bits. The mathematical value of the result is exactly representable as a 32-bit two's complement bit string.