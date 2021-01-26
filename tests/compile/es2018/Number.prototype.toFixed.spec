          1. Let _x_ be ? thisNumberValue(*this* value).
          1. Let _f_ be ? ToInteger(_fractionDigits_). (If _fractionDigits_ is *undefined*, this step produces the value 0.)
          1. If _f_ < 0 or _f_ > 100, throw a *RangeError* exception.
          1. If _x_ is *NaN*, return the String `"NaN"`.
          1. Let _s_ be the empty String.
          1. If _x_ < 0, then
            1. Let _s_ be `"-"`.
            1. Let _x_ be -_x_.
          1. If _x_ ≥ 10<sup>21</sup>, then
            1. Let _m_ be ! ToString(_x_).
          1. Else _x_ < 10<sup>21</sup>,
            1. Let _n_ be an integer for which the exact mathematical value of _n_ ÷ 10<sup>_f_</sup> - _x_ is as close to zero as possible. If there are two such _n_, pick the larger _n_.
            1. If _n_ = 0, let _m_ be the String `"0"`. Otherwise, let _m_ be the String value consisting of the digits of the decimal representation of _n_ (in order, with no leading zeroes).
            1. If _f_ ≠ 0, then
              1. Let _k_ be the length of _m_.
              1. If _k_ ≤ _f_, then
                1. Let _z_ be the String value consisting of _f_+1-_k_ occurrences of the code unit 0x0030 (DIGIT ZERO).
                1. Let _m_ be the string-concatenation of _z_ and _m_.
                1. Let _k_ be _f_ + 1.
              1. Let _a_ be the first _k_-_f_ elements of _m_, and let _b_ be the remaining _f_ elements of _m_.
              1. Let _m_ be the string-concatenation of _a_, `"."`, and _b_.
          1. Return the string-concatenation of _s_ and _m_.