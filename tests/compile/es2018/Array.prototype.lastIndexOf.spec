          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If _len_ is 0, return -1.
          1. If _fromIndex_ is present, let _n_ be ? ToInteger(_fromIndex_); else let _n_ be _len_-1.
          1. If _n_ ≥ 0, then
            1. If _n_ is *-0*, let _k_ be *+0*; else let _k_ be min(_n_, _len_ - 1).
          1. Else _n_ < 0,
            1. Let _k_ be _len_ + _n_.
          1. Repeat, while _k_ ≥ 0
            1. Let _kPresent_ be ? HasProperty(_O_, ! ToString(_k_)).
            1. If _kPresent_ is *true*, then
              1. Let _elementK_ be ? Get(_O_, ! ToString(_k_)).
              1. Let _same_ be the result of performing Strict Equality Comparison _searchElement_ === _elementK_.
              1. If _same_ is *true*, return _k_.
            1. Decrease _k_ by 1.
          1. Return -1.