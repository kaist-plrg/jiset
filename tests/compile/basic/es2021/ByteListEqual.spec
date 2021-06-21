          1. If _xBytes_ and _yBytes_ do not have the same number of elements, return *false*.
          1. Let _i_ be 0.
          1. For each element _xByte_ of _xBytes_, do
            1. Let _yByte_ be _yBytes_[_i_].
            1. If _xByte_ ≠ _yByte_, return *false*.
            1. Set _i_ to _i_ + 1.
          1. Return *true*.