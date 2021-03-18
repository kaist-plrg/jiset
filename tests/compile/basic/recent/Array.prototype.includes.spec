          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. If _len_ is 0, return *false*.
          1. Let _n_ be ? ToIntegerOrInfinity(_fromIndex_).
          1. Assert: If _fromIndex_ is *undefined*, then _n_ is 0.
          1. If _n_ is +∞, return *false*.
          1. Else if _n_ is -∞, set _n_ to 0.
          1. If _n_ ≥ 0, then
            1. Let _k_ be _n_.
          1. Else,
            1. Let _k_ be _len_ + _n_.
            1. If _k_ < 0, set _k_ to 0.
          1. Repeat, while _k_ < _len_,
            1. Let _elementK_ be ? Get(_O_, ! ToString(𝔽(_k_))).
            1. If SameValueZero(_searchElement_, _elementK_) is *true*, return *true*.
            1. Set _k_ to _k_ + 1.
          1. Return *false*.