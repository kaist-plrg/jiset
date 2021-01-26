          1. Let _x_ be ? thisNumberValue(*this* value).
          1. If _radix_ is not present, let _radixNumber_ be 10.
          1. Else if _radix_ is *undefined*, let _radixNumber_ be 10.
          1. Else, let _radixNumber_ be ? ToInteger(_radix_).
          1. If _radixNumber_ < 2 or _radixNumber_ > 36, throw a *RangeError* exception.
          1. If _radixNumber_ = 10, return ! ToString(_x_).
          1. Return the String representation of this Number value using the radix specified by _radixNumber_. Letters `a`-`z` are used for digits with values 10 through 35. The precise algorithm is implementation-dependent, however the algorithm should be a generalization of that specified in <emu-xref href="#sec-tostring-applied-to-the-number-type"></emu-xref>.