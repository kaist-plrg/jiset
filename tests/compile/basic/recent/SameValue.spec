        1. If Type(_x_) is different from Type(_y_), return *false*.
        1. If Type(_x_) is Number or BigInt, then
          1. Return ! Type(_x_)::sameValue(_x_, _y_).
        1. Return ! SameValueNonNumeric(_x_, _y_).