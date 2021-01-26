          1. Let _n_ be ? ToNumber(_x_).
          1. If _n_ is *NaN*, return *NaN*.
          1. If _n_ is one of *+0*<sub>𝔽</sub>, *-0*<sub>𝔽</sub>, *+∞*<sub>𝔽</sub>, or *-∞*<sub>𝔽</sub>, return _n_.
          1. Let _n32_ be the result of converting _n_ to a value in IEEE 754-2019 binary32 format using roundTiesToEven mode.
          1. Let _n64_ be the result of converting _n32_ to a value in IEEE 754-2019 binary64 format.
          1. Return the ECMAScript Number value corresponding to _n64_.