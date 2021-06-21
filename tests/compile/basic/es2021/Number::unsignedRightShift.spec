            1. Let _lnum_ be ! ToUint32(_x_).
            1. Let _rnum_ be ! ToUint32(_y_).
            1. Let _shiftCount_ be ℝ(_rnum_) modulo 32.
            1. Return the result of performing a zero-filling right shift of _lnum_ by _shiftCount_ bits. Vacated bits are filled with zero. The mathematical value of the result is exactly representable as a 32-bit unsigned bit string.