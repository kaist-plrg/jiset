          1. Return a new Matcher with parameters (_x_, _c_) that captures nothing and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Let _e_ be _x_'s _endIndex_.
            1. If _e_ = 0, or if _Multiline_ is *true* and the character _Input_[_e_ - 1] is one of |LineTerminator|, then
              1. Return _c_(_x_).
            1. Return ~failure~.