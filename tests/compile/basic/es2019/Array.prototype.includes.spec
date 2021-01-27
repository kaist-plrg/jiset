          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If _len_ is 0, return *false*.
          1. Let _n_ be ? ToInteger(_fromIndex_).
          1. Assert: If _fromIndex_ is *undefined*, then _n_ is 0.
          1. If _n_ ≥ 0, then
            1. Let _k_ be _n_.
          1. Else _n_ < 0,
            1. Let _k_ be _len_ + _n_.
            1. If _k_ < 0, set _k_ to 0.
          1. Repeat, while _k_ < _len_
            1. Let _elementK_ be the result of ? Get(_O_, ! ToString(_k_)).
            1. If SameValueZero(_searchElement_, _elementK_) is *true*, return *true*.
            1. Increase _k_ by 1.
          1. Return *false*.