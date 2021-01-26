        1. Assert: Type(_x_) is not Number or BigInt.
        1. Assert: Type(_x_) is the same as Type(_y_).
        1. If Type(_x_) is Undefined, return *true*.
        1. If Type(_x_) is Null, return *true*.
        1. If Type(_x_) is String, then
          1. If _x_ and _y_ are exactly the same sequence of code units (same length and same code units at corresponding indices), return *true*; otherwise, return *false*.
        1. If Type(_x_) is Boolean, then
          1. If _x_ and _y_ are both *true* or both *false*, return *true*; otherwise, return *false*.
        1. If Type(_x_) is Symbol, then
          1. If _x_ and _y_ are both the same Symbol value, return *true*; otherwise, return *false*.
        1. If _x_ and _y_ are the same Object value, return *true*. Otherwise, return *false*.