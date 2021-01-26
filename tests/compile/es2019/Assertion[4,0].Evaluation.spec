          1. Evaluate |Disjunction| with +1 as its _direction_ argument to obtain a Matcher _m_.
          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps:
            1. Let _d_ be a Continuation that always returns its State argument as a successful MatchResult.
            1. Call _m_(_x_, _d_) and let _r_ be its result.
            1. If _r_ is ~failure~, return ~failure~.
            1. Let _y_ be _r_'s State.
            1. Let _cap_ be _y_'s _captures_ List.
            1. Let _xe_ be _x_'s _endIndex_.
            1. Let _z_ be the State (_xe_, _cap_).
            1. Call _c_(_z_) and return its result.