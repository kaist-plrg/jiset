        1. Let _number_ be ? ToNumber(_argument_).
        1. If _number_ is *NaN*, return *+0*<sub>𝔽</sub>.
        1. If ℝ(_number_) ≤ 0, return *+0*<sub>𝔽</sub>.
        1. If ℝ(_number_) ≥ 255, return *255*<sub>𝔽</sub>.
        1. Let _f_ be floor(ℝ(_number_)).
        1. If _f_ + 0.5 < ℝ(_number_), return 𝔽(_f_ + 1).
        1. If ℝ(_number_) < _f_ + 0.5, return 𝔽(_f_).
        1. If _f_ is odd, return 𝔽(_f_ + 1).
        1. Return 𝔽(_f_).