          1. Evaluate |Alternative| to obtain a Matcher _m1_.
          1. Evaluate |Term| to obtain a Matcher _m2_.
          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps when evaluated:
            1. Let _d_ be a Continuation that takes a State argument _y_ and returns the result of calling _m2_(_y_, _c_).
            1. Call _m1_(_x_, _d_) and return its result.