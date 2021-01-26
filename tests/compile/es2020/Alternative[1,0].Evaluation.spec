          1. Evaluate |Alternative| with argument _direction_ to obtain a Matcher _m1_.
          1. Evaluate |Term| with argument _direction_ to obtain a Matcher _m2_.
          1. If _direction_ is equal to +1, then
            1. Return a new Matcher with parameters (_x_, _c_) that captures _m1_ and _m2_ and performs the following steps when called:
              1. Assert: _x_ is a State.
              1. Assert: _c_ is a Continuation.
              1. Let _d_ be a new Continuation with parameters (_y_) that captures _c_ and _m2_ and performs the following steps when called:
                1. Assert: _y_ is a State.
                1. Call _m2_(_y_, _c_) and return its result.
              1. Call _m1_(_x_, _d_) and return its result.
          1. Else,
            1. Assert: _direction_ is equal to -1.
            1. Return a new Matcher with parameters (_x_, _c_) that captures _m1_ and _m2_ and performs the following steps when called:
              1. Assert: _x_ is a State.
              1. Assert: _c_ is a Continuation.
              1. Let _d_ be a new Continuation with parameters (_y_) that captures _c_ and _m1_ and performs the following steps when called:
                1. Assert: _y_ is a State.
                1. Call _m1_(_y_, _c_) and return its result.
              1. Call _m2_(_x_, _d_) and return its result.