            1. If _max_ = 0, return _c_(_x_).
            1. Let _d_ be a new Continuation with parameters (_y_) that captures _m_, _min_, _max_, _greedy_, _x_, _c_, _parenIndex_, and _parenCount_ and performs the following steps when called:
              1. Assert: _y_ is a State.
              1. [id="step-repeatmatcher-done"] If _min_ = 0 and _y_'s _endIndex_ = _x_'s _endIndex_, return ~failure~.
              1. If _min_ = 0, let _min2_ be 0; otherwise let _min2_ be _min_ - 1.
              1. If _max_ is +∞, let _max2_ be +∞; otherwise let _max2_ be _max_ - 1.
              1. Return ! RepeatMatcher(_m_, _min2_, _max2_, _greedy_, _y_, _c_, _parenIndex_, _parenCount_).
            1. Let _cap_ be a copy of _x_'s _captures_ List.
            1. [id="step-repeatmatcher-clear-captures"] For each integer _k_ that satisfies _parenIndex_ < _k_ and _k_ ≤ _parenIndex_ + _parenCount_, set _cap_[_k_] to *undefined*.
            1. Let _e_ be _x_'s _endIndex_.
            1. Let _xr_ be the State (_e_, _cap_).
            1. If _min_ ≠ 0, return _m_(_xr_, _d_).
            1. If _greedy_ is *false*, then
              1. Let _z_ be _c_(_x_).
              1. If _z_ is not ~failure~, return _z_.
              1. Return _m_(_xr_, _d_).
            1. Let _z_ be _m_(_xr_, _d_).
            1. If _z_ is not ~failure~, return _z_.
            1. Return _c_(_x_).