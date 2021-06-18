          1. Let _n_ be ? ToNumber(_x_).
          1. If _n_ is *NaN*, _n_ is *+0*<sub>𝔽</sub>, _n_ is *-0*<sub>𝔽</sub>, _n_ is *+∞*<sub>𝔽</sub>, or _n_ is *-∞*<sub>𝔽</sub>, return _n_.
          1. If _n_ < *1*<sub>𝔽</sub> and _n_ > *+0*<sub>𝔽</sub>, return *+0*<sub>𝔽</sub>.
          1. If _n_ < *+0*<sub>𝔽</sub> and _n_ > *-1*<sub>𝔽</sub>, return *-0*<sub>𝔽</sub>.
          1. Return the integral Number nearest _n_ in the direction of *+0*<sub>𝔽</sub>.