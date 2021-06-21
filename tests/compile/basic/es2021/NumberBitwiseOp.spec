            1. Assert: _op_ is `&`, `^`, or `|`.
            1. Let _lnum_ be ! ToInt32(_x_).
            1. Let _rnum_ be ! ToInt32(_y_).
            1. Let _lbits_ be the 32-bit two's complement bit string representing ℝ(_lnum_).
            1. Let _rbits_ be the 32-bit two's complement bit string representing ℝ(_rnum_).
            1. If _op_ is `&`, let _result_ be the result of applying the bitwise AND operation to _lbits_ and _rbits_.
            1. Else if _op_ is `^`, let _result_ be the result of applying the bitwise exclusive OR (XOR) operation to _lbits_ and _rbits_.
            1. Else, _op_ is `|`. Let _result_ be the result of applying the bitwise inclusive OR operation to _lbits_ and _rbits_.
            1. Return the Number value for the integer represented by the 32-bit two's complement bit string _result_.