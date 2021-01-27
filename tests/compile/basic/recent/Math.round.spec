          1. Let _n_ be ? ToNumber(_x_).
          1. If _n_ is *NaN*, *+∞*<sub>𝔽</sub>, *-∞*<sub>𝔽</sub>, or an integral Number, return _n_.
          1. If _n_ < *0.5*<sub>𝔽</sub> and _n_ > *+0*<sub>𝔽</sub>, return *+0*<sub>𝔽</sub>.
          1. If _n_ < *+0*<sub>𝔽</sub> and _n_ ≥ *-0.5*<sub>𝔽</sub>, return *-0*<sub>𝔽</sub>.
          1. Return the integral Number closest to _n_, preferring the Number closer to +∞ in the case of a tie.