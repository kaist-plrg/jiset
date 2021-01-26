          1. Let _n_ be ? ToNumber(_x_).
          1. If _n_ is *NaN*, _n_ is *+0*<sub>𝔽</sub>, or _n_ is *-0*<sub>𝔽</sub>, return _n_.
          1. If _n_ > *1*<sub>𝔽</sub> or _n_ < *-1*<sub>𝔽</sub>, return *NaN*.
          1. If _n_ is *1*<sub>𝔽</sub>, return *+∞*<sub>𝔽</sub>.
          1. If _n_ is *-1*<sub>𝔽</sub>, return *-∞*<sub>𝔽</sub>.
          1. Return an implementation-approximated value representing the result of the inverse hyperbolic tangent of ℝ(_n_).