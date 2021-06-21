            1. If _x_ is *NaN*, return *undefined*.
            1. If _y_ is *NaN*, return *undefined*.
            1. If _x_ and _y_ are the same Number value, return *false*.
            1. If _x_ is *+0*<sub>𝔽</sub> and _y_ is *-0*<sub>𝔽</sub>, return *false*.
            1. If _x_ is *-0*<sub>𝔽</sub> and _y_ is *+0*<sub>𝔽</sub>, return *false*.
            1. If _x_ is *+∞*<sub>𝔽</sub>, return *false*.
            1. If _y_ is *+∞*<sub>𝔽</sub>, return *true*.
            1. If _y_ is *-∞*<sub>𝔽</sub>, return *false*.
            1. If _x_ is *-∞*<sub>𝔽</sub>, return *true*.
            1. Assert: _x_ and _y_ are finite and non-zero.
            1. If ℝ(_x_) < ℝ(_y_), return *true*. Otherwise, return *false*.