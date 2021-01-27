            1. Assert: _op_ is *"&"*, *"|"*, or *"^"*.
            1. Let _result_ be *0n*.
            1. Let _shift_ be 0.
            1. Repeat, until (_x_ = 0 or _x_ = -1) and (_y_ = 0 or _y_ = -1),
              1. Let _xDigit_ be _x_ modulo 2.
              1. Let _yDigit_ be _y_ modulo 2.
              1. If _op_ is *"&"*, set _result_ to _result_ + 2<sup>_shift_</sup> × BinaryAnd(_xDigit_, _yDigit_).
              1. Else if _op_ is *"|"*, set _result_ to _result_ + 2<sup>_shift_</sup> × BinaryOr(_xDigit_, _yDigit_).
              1. Else,
                1. Assert: _op_ is *"^"*.
                1. Set _result_ to _result_ + 2<sup>_shift_</sup> × BinaryXor(_xDigit_, _yDigit_).
              1. Set _shift_ to _shift_ + 1.
              1. Set _x_ to (_x_ - _xDigit_) / 2.
              1. Set _y_ to (_y_ - _yDigit_) / 2.
            1. If _op_ is *"&"*, let _tmp_ be BinaryAnd(_x_ modulo 2, _y_ modulo 2).
            1. Else if _op_ is *"|"*, let _tmp_ be BinaryOr(_x_ modulo 2, _y_ modulo 2).
            1. Else,
              1. Assert: _op_ is *"^"*.
              1. Let _tmp_ be BinaryXor(_x_ modulo 2, _y_ modulo 2).
            1. If _tmp_ ≠ 0, then
              1. Set _result_ to _result_ - 2<sup>_shift_</sup>.
              1. NOTE: This extends the sign.
            1. Return _result_.