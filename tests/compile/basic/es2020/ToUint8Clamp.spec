        1. Let _number_ be ? ToNumber(_argument_).
        1. If _number_ is *NaN*, return *+0*.
        1. If _number_ ≤ 0, return *+0*.
        1. If _number_ ≥ 255, return 255.
        1. Let _f_ be floor(_number_).
        1. If _f_ + 0.5 < _number_, return _f_ + 1.
        1. If _number_ < _f_ + 0.5, return _f_.
        1. If _f_ is odd, return _f_ + 1.
        1. Return _f_.