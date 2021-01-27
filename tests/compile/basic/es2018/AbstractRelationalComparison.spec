        1. If the _LeftFirst_ flag is *true*, then
          1. Let _px_ be ? ToPrimitive(_x_, hint Number).
          1. Let _py_ be ? ToPrimitive(_y_, hint Number).
        1. Else the order of evaluation needs to be reversed to preserve left to right evaluation,
          1. Let _py_ be ? ToPrimitive(_y_, hint Number).
          1. Let _px_ be ? ToPrimitive(_x_, hint Number).
        1. If Type(_px_) is String and Type(_py_) is String, then
          1. If IsStringPrefix(_py_, _px_) is *true*, return *false*.
          1. If IsStringPrefix(_px_, _py_) is *true*, return *true*.
          1. Let _k_ be the smallest nonnegative integer such that the code unit at index _k_ within _px_ is different from the code unit at index _k_ within _py_. (There must be such a _k_, for neither String is a prefix of the other.)
          1. Let _m_ be the integer that is the numeric value of the code unit at index _k_ within _px_.
          1. Let _n_ be the integer that is the numeric value of the code unit at index _k_ within _py_.
          1. If _m_ < _n_, return *true*. Otherwise, return *false*.
        1. Else,
          1. NOTE: Because _px_ and _py_ are primitive values evaluation order is not important.
          1. Let _nx_ be ? ToNumber(_px_).
          1. Let _ny_ be ? ToNumber(_py_).
          1. If _nx_ is *NaN*, return *undefined*.
          1. If _ny_ is *NaN*, return *undefined*.
          1. If _nx_ and _ny_ are the same Number value, return *false*.
          1. If _nx_ is *+0* and _ny_ is *-0*, return *false*.
          1. If _nx_ is *-0* and _ny_ is *+0*, return *false*.
          1. If _nx_ is *+∞*, return *false*.
          1. If _ny_ is *+∞*, return *true*.
          1. If _ny_ is *-∞*, return *false*.
          1. If _nx_ is *-∞*, return *true*.
          1. If the mathematical value of _nx_ is less than the mathematical value of _ny_—note that these mathematical values are both finite and not both zero—return *true*. Otherwise, return *false*.