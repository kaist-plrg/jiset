          1. Evaluate |Alternative| with argument _direction_ to obtain a Matcher _m1_.
          1. Evaluate |Disjunction| with argument _direction_ to obtain a Matcher _m2_.
          1. Return a new Matcher with parameters (_x_, _c_) that captures _m1_ and _m2_ and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Let _r_ be _m1_(_x_, _c_).
            1. If _r_ is not ~failure~, return _r_.
            1. Return _m2_(_x_, _c_).