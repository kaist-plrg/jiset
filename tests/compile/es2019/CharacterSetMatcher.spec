            1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps when evaluated:
              1. Let _e_ be _x_'s _endIndex_.
              1. Let _f_ be _e_ + _direction_.
              1. If _f_ < 0 or _f_ > _InputLength_, return ~failure~.
              1. Let _index_ be min(_e_, _f_).
              1. Let _ch_ be the character _Input_[_index_].
              1. Let _cc_ be Canonicalize(_ch_).
              1. If _invert_ is *false*, then
                1. If there does not exist a member _a_ of set _A_ such that Canonicalize(_a_) is _cc_, return ~failure~.
              1. Else,
                1. Assert: _invert_ is *true*.
                1. If there exists a member _a_ of set _A_ such that Canonicalize(_a_) is _cc_, return ~failure~.
              1. Let _cap_ be _x_'s _captures_ List.
              1. Let _y_ be the State (_f_, _cap_).
              1. Call _c_(_y_) and return its result.