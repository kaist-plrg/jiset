        1. Let _number_ be ? ToNumber(_argument_).
        1. If _number_ is *NaN*, return *+0*.
        1. If _number_ is *+0*, *-0*, *+∞*, or *-∞*, return _number_.
        1. Return the number value that is the same sign as _number_ and whose magnitude is floor(abs(_number_)).