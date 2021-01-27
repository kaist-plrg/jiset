          1. Let _x_ be ? thisNumberValue(*this* value).
          1. Let _f_ be ? ToIntegerOrInfinity(_fractionDigits_).
          1. Assert: If _fractionDigits_ is *undefined*, then _f_ is 0.
          1. If _f_ is not finite, throw a *RangeError* exception.
          1. If _f_ < 0 or _f_ > 100, throw a *RangeError* exception.
          1. If _x_ is not finite, return ! Number::toString(_x_).
          1. Set _x_ to ℝ(_x_).
          1. Let _s_ be the empty String.
          1. If _x_ < 0, then
            1. Set _s_ to *"-"*.
            1. Set _x_ to -_x_.
          1. If _x_ ≥ 10<sup>21</sup>, then
            1. Let _m_ be ! ToString(𝔽(_x_)).
          1. Else,
            1. Let _n_ be an integer for which _n_ ÷ 10<sup>_f_</sup> - _x_ is as close to zero as possible. If there are two such _n_, pick the larger _n_.
            1. If _n_ = 0, let _m_ be the String *"0"*. Otherwise, let _m_ be the String value consisting of the digits of the decimal representation of _n_ (in order, with no leading zeroes).
            1. If _f_ ≠ 0, then
              1. Let _k_ be the length of _m_.
              1. If _k_ ≤ _f_, then
                1. Let _z_ be the String value consisting of _f_ + 1 - _k_ occurrences of the code unit 0x0030 (DIGIT ZERO).
                1. Set _m_ to the string-concatenation of _z_ and _m_.
                1. Set _k_ to _f_ + 1.
              1. Let _a_ be the first _k_ - _f_ code units of _m_.
              1. Let _b_ be the other _f_ code units of _m_.
              1. Set _m_ to the string-concatenation of _a_, *"."*, and _b_.
          1. Return the string-concatenation of _s_ and _m_.