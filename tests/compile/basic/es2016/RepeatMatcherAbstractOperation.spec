            1. If _max_ is zero, return _c_(_x_).
            1. Create an internal Continuation closure _d_ that takes one State argument _y_ and performs the following steps when evaluated:
              1. If _min_ is zero and _y_'s _endIndex_ is equal to _x_'s _endIndex_, return ~failure~.
              1. If _min_ is zero, let _min2_ be zero; otherwise let _min2_ be _min_-1.
              1. If _max_ is ∞, let _max2_ be ∞; otherwise let _max2_ be _max_-1.
              1. Call RepeatMatcher(_m_, _min2_, _max2_, _greedy_, _y_, _c_, _parenIndex_, _parenCount_) and return its result.
            1. Let _cap_ be a fresh copy of _x_'s _captures_ List.
            1. For every integer _k_ that satisfies _parenIndex_ < _k_ and _k_ ≤ _parenIndex_+_parenCount_, set _cap_[_k_] to *undefined*.
            1. Let _e_ be _x_'s _endIndex_.
            1. Let _xr_ be the State (_e_, _cap_).
            1. If _min_ is not zero, return _m_(_xr_, _d_).
            1. If _greedy_ is *false*, then
              1. Call _c_(_x_) and let _z_ be its result.
              1. If _z_ is not ~failure~, return _z_.
              1. Call _m_(_xr_, _d_) and return its result.
            1. Call _m_(_xr_, _d_) and let _z_ be its result.
            1. If _z_ is not ~failure~, return _z_.
            1. Call _c_(_x_) and return its result.