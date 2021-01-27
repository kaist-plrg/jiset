          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps when evaluated:
            1. Evaluate |Assertion| to obtain an AssertionTester _t_.
            1. Call _t_(_x_) and let _r_ be the resulting Boolean value.
            1. If _r_ is *false*, return ~failure~.
            1. Call _c_(_x_) and return its result.