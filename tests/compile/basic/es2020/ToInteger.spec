        1. Let _number_ be ? ToNumber(_argument_).
        1. If _number_ is *NaN*, *+0*, or *-0*, return *+0*.
        1. If _number_ is *+∞* or *-∞*, return _number_.
        1. Let _integer_ be the Number value that is the same sign as _number_ and whose magnitude is floor(abs(_number_)).
        1. If _integer_ is *-0*, return *+0*.
        1. Return _integer_.