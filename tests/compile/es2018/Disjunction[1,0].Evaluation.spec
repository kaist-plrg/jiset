          1. Evaluate |Alternative| with argument _direction_ to obtain a Matcher _m1_.
          1. Evaluate |Disjunction| with argument _direction_ to obtain a Matcher _m2_.
          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps when evaluated:
            1. Call _m1_(_x_, _c_) and let _r_ be its result.
            1. If _r_ is not ~failure~, return _r_.
            1. Call _m2_(_x_, _c_) and return its result.