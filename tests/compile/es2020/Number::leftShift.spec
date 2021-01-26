            1. Let _lnum_ be ! ToInt32(_x_).
            1. Let _rnum_ be ! ToUint32(_y_).
            1. Let _shiftCount_ be the result of masking out all but the least significant 5 bits of _rnum_, that is, compute _rnum_ & 0x1F.
            1. Return the result of left shifting _lnum_ by _shiftCount_ bits. The result is a signed 32-bit integer.