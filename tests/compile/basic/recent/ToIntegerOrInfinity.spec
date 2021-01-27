        1. Let _number_ be ? ToNumber(_argument_).
        1. If _number_ is *NaN*, *+0*<sub>𝔽</sub>, or *-0*<sub>𝔽</sub>, return 0.
        1. If _number_ is *+∞*<sub>𝔽</sub>, return +∞.
        1. If _number_ is *-∞*<sub>𝔽</sub>, return -∞.
        1. Let _integer_ be floor(abs(ℝ(_number_))).
        1. If _number_ < *+0*<sub>𝔽</sub>, set _integer_ to -_integer_.
        1. Return _integer_.