          1. Return a new Matcher with parameters (_x_, _c_) that captures nothing and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Let _e_ be _x_'s _endIndex_.
            1. Call IsWordChar(_e_ - 1) and let _a_ be the Boolean result.
            1. Call IsWordChar(_e_) and let _b_ be the Boolean result.
            1. If _a_ is *true* and _b_ is *false*, or if _a_ is *false* and _b_ is *true*, then
              1. Call _c_(_x_) and return its result.
            1. Return ~failure~.