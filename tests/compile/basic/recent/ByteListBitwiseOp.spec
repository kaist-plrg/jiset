          1. Assert: _op_ is `&`, `^`, or `|`.
          1. Assert: _xBytes_ and _yBytes_ have the same number of elements.
          1. Let _result_ be a new empty List.
          1. Let _i_ be 0.
          1. For each element _xByte_ of _xBytes_, do
            1. Let _yByte_ be _yBytes_[_i_].
            1. If _op_ is `&`, let _resultByte_ be the result of applying the bitwise AND operation to _xByte_ and _yByte_.
            1. Else if _op_ is `^`, let _resultByte_ be the result of applying the bitwise exclusive OR (XOR) operation to _xByte_ and _yByte_.
            1. Else, _op_ is `|`. Let _resultByte_ be the result of applying the bitwise inclusive OR operation to _xByte_ and _yByte_.
            1. Set _i_ to _i_ + 1.
            1. Append _resultByte_ to the end of _result_.
          1. Return _result_.