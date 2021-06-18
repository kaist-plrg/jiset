          1. Let _n_ be ? ToNumber(_x_).
          1. If _n_ is *NaN*, _n_ is *+0*<sub>𝔽</sub>, or _n_ is *-0*<sub>𝔽</sub>, return _n_.
          1. If _n_ is *+∞*<sub>𝔽</sub>, return an implementation-approximated value representing π / 2.
          1. If _n_ is *-∞*<sub>𝔽</sub>, return an implementation-approximated value representing -π / 2.
          1. Return an implementation-approximated value representing the result of the inverse tangent of ℝ(_n_).