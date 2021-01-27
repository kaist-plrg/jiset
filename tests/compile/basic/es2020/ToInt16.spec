        1. Let _number_ be ? ToNumber(_argument_).
        1. If _number_ is *NaN*, *+0*, *-0*, *+∞*, or *-∞*, return *+0*.
        1. Let _int_ be the Number value that is the same sign as _number_ and whose magnitude is floor(abs(_number_)).
        1. Let _int16bit_ be _int_ modulo 2<sup>16</sup>.
        1. If _int16bit_ ≥ 2<sup>15</sup>, return _int16bit_ - 2<sup>16</sup>; otherwise return _int16bit_.