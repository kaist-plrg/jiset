        1. If the _LeftFirst_ flag is *true*, then
          1. Let _px_ be ? ToPrimitive(_x_, hint Number).
          1. Let _py_ be ? ToPrimitive(_y_, hint Number).
        1. Else the order of evaluation needs to be reversed to preserve left to right evaluation
          1. Let _py_ be ? ToPrimitive(_y_, hint Number).
          1. Let _px_ be ? ToPrimitive(_x_, hint Number).
        1. If both _px_ and _py_ are Strings, then
          1. If _py_ is a prefix of _px_, return *false*. (A String value _p_ is a prefix of String value _q_ if _q_ can be the result of concatenating _p_ and some other String _r_. Note that any String is a prefix of itself, because _r_ may be the empty String.)
          1. If _px_ is a prefix of _py_, return *true*.
          1. Let _k_ be the smallest nonnegative integer such that the code unit at index _k_ within _px_ is different from the code unit at index _k_ within _py_. (There must be such a _k_, for neither String is a prefix of the other.)
          1. Let _m_ be the integer that is the code unit value at index _k_ within _px_.
          1. Let _n_ be the integer that is the code unit value at index _k_ within _py_.
          1. If _m_ < _n_, return *true*. Otherwise, return *false*.
        1. Else,
          1. Let _nx_ be ? ToNumber(_px_). Because _px_ and _py_ are primitive values evaluation order is not important.
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
          1. If the mathematical value of _nx_ is less than the mathematical value of _ny_ —note that these mathematical values are both finite and not both zero—return *true*. Otherwise, return *false*.