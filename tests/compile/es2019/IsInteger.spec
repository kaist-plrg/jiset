        1. If Type(_argument_) is not Number, return *false*.
        1. If _argument_ is *NaN*, *+∞*, or *-∞*, return *false*.
        1. If floor(abs(_argument_)) ≠ abs(_argument_), return *false*.
        1. Return *true*.