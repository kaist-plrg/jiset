          1. Evaluate |Disjunction| with 1 as its _direction_ argument to obtain a Matcher _m_.
          1. Return a new Matcher with parameters (_x_, _c_) that captures _m_ and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Let _d_ be a new Continuation with parameters (_y_) that captures nothing and performs the following steps when called:
              1. Assert: _y_ is a State.
              1. Return _y_.
            1. Let _r_ be _m_(_x_, _d_).
            1. If _r_ is not ~failure~, return ~failure~.
            1. Return _c_(_x_).