          1. Let _coerced_ be a new empty List.
          1. For each element _arg_ of _args_, do
            1. Let _n_ be ? ToNumber(_arg_).
            1. Append _n_ to _coerced_.
          1. Let _lowest_ be *+∞*<sub>𝔽</sub>.
          1. For each element _number_ of _coerced_, do
            1. If _number_ is *NaN*, return *NaN*.
            1. If _number_ is *-0*<sub>𝔽</sub> and _lowest_ is *+0*<sub>𝔽</sub>, set _lowest_ to *-0*<sub>𝔽</sub>.
            1. If _number_ < _lowest_, set _lowest_ to _number_.
          1. Return _lowest_.