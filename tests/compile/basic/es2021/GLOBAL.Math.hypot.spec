          1. Let _coerced_ be a new empty List.
          1. For each element _arg_ of _args_, do
            1. Let _n_ be ? ToNumber(_arg_).
            1. Append _n_ to _coerced_.
          1. For each element _number_ of _coerced_, do
            1. If _number_ is *+∞*<sub>𝔽</sub> or _number_ is *-∞*<sub>𝔽</sub>, return *+∞*<sub>𝔽</sub>.
          1. Let _onlyZero_ be *true*.
          1. For each element _number_ of _coerced_, do
            1. If _number_ is *NaN*, return *NaN*.
            1. If _number_ is neither *+0*<sub>𝔽</sub> nor *-0*<sub>𝔽</sub>, set _onlyZero_ to *false*.
          1. If _onlyZero_ is *true*, return *+0*<sub>𝔽</sub>.
          1. Return an implementation-approximated value representing the square root of the sum of squares of the mathematical values of the elements of _coerced_.