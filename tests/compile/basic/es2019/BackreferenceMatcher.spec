            1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps:
              1. Let _cap_ be _x_'s _captures_ List.
              1. Let _s_ be _cap_[_n_].
              1. If _s_ is *undefined*, return _c_(_x_).
              1. Let _e_ be _x_'s _endIndex_.
              1. Let _len_ be the number of elements in _s_.
              1. Let _f_ be _e_ + _direction_ × _len_.
              1. If _f_ < 0 or _f_ > _InputLength_, return ~failure~.
              1. Let _g_ be min(_e_, _f_).
              1. If there exists an integer _i_ between 0 (inclusive) and _len_ (exclusive) such that Canonicalize(_s_[_i_]) is not the same character value as Canonicalize(_Input_[_g_ + _i_]), return ~failure~.
              1. Let _y_ be the State (_f_, _cap_).
              1. Call _c_(_y_) and return its result.