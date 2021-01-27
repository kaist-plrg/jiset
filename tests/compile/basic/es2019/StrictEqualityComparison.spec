        1. If Type(_x_) is different from Type(_y_), return *false*.
        1. If Type(_x_) is Number, then
          1. If _x_ is *NaN*, return *false*.
          1. If _y_ is *NaN*, return *false*.
          1. If _x_ is the same Number value as _y_, return *true*.
          1. If _x_ is *+0* and _y_ is *-0*, return *true*.
          1. If _x_ is *-0* and _y_ is *+0*, return *true*.
          1. Return *false*.
        1. Return SameValueNonNumber(_x_, _y_).