          1. Let _n_ be ? ToNumber(_x_).
          1. If _n_ is *NaN* or _n_ is *+∞*<sub>𝔽</sub>, return _n_.
          1. If _n_ is *1*<sub>𝔽</sub>, return *+0*<sub>𝔽</sub>.
          1. If _n_ is *+0*<sub>𝔽</sub> or _n_ is *-0*<sub>𝔽</sub>, return *-∞*<sub>𝔽</sub>.
          1. If _n_ < *+0*<sub>𝔽</sub>, return *NaN*.
          1. Return an implementation-approximated value representing the result of the natural logarithm of ℝ(_n_).