          1. Return a new Matcher with parameters (_x_, _c_) that captures nothing and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Call _c_(_x_) and return its result.