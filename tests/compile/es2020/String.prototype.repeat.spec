          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _n_ be ? ToInteger(_count_).
          1. If _n_ < 0, throw a *RangeError* exception.
          1. If _n_ is *+∞*, throw a *RangeError* exception.
          1. If _n_ is 0, return the empty String.
          1. Return the String value that is made from _n_ copies of _S_ appended together.