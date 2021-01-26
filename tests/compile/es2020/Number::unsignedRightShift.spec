            1. Let _lnum_ be ! ToUint32(_x_).
            1. Let _rnum_ be ! ToUint32(_y_).
            1. Let _shiftCount_ be the result of masking out all but the least significant 5 bits of _rnum_, that is, compute _rnum_ & 0x1F.
            1. Return the result of performing a zero-filling right shift of _lnum_ by _shiftCount_ bits. Vacated bits are filled with zero. The result is an unsigned 32-bit integer.