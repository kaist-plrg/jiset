          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. If _len_ is 0, return *-1*<sub>𝔽</sub>.
          1. If _fromIndex_ is present, let _n_ be ? ToIntegerOrInfinity(_fromIndex_); else let _n_ be _len_ - 1.
          1. If _n_ is -∞, return *-1*<sub>𝔽</sub>.
          1. If _n_ ≥ 0, then
            1. Let _k_ be min(_n_, _len_ - 1).
          1. Else,
            1. Let _k_ be _len_ + _n_.
          1. Repeat, while _k_ ≥ 0,
            1. Let _kPresent_ be ? HasProperty(_O_, ! ToString(𝔽(_k_))).
            1. If _kPresent_ is *true*, then
              1. Let _elementK_ be ? Get(_O_, ! ToString(𝔽(_k_))).
              1. Let _same_ be the result of performing Strict Equality Comparison _searchElement_ === _elementK_.
              1. If _same_ is *true*, return 𝔽(_k_).
            1. Set _k_ to _k_ - 1.
          1. Return *-1*<sub>𝔽</sub>.