          1. Let _x_ be ? thisNumberValue(*this* value).
          1. Let _f_ be ? ToInteger(_fractionDigits_).
          1. Assert: _f_ is 0, when _fractionDigits_ is *undefined*.
          1. If _x_ is *NaN*, return the String `"NaN"`.
          1. Let _s_ be the empty String.
          1. If _x_ < 0, then
            1. Let _s_ be `"-"`.
            1. Let _x_ be -_x_.
          1. If _x_ = *+∞*, then
            1. Return the concatenation of the Strings _s_ and `"Infinity"`.
          1. If _f_ < 0 or _f_ > 20, throw a *RangeError* exception. However, an implementation is permitted to extend the behaviour of `toExponential` for values of _f_ less than 0 or greater than 20. In this case `toExponential` would not necessarily throw *RangeError* for such values.
          1. If _x_ = 0, then
            1. Let _m_ be the String consisting of _f_+1 occurrences of the code unit 0x0030 (DIGIT ZERO).
            1. Let _e_ be 0.
          1. Else _x_ ≠ 0,
            1. If _fractionDigits_ is not *undefined*, then
              1. Let _e_ and _n_ be integers such that 10<sup>_f_</sup> ≤ _n_ < 10<sup>_f_+1</sup> and for which the exact mathematical value of _n_ × 10<sup>_e_-_f_</sup> - _x_ is as close to zero as possible. If there are two such sets of _e_ and _n_, pick the _e_ and _n_ for which _n_ × 10<sup>_e_-_f_</sup> is larger.
            1. Else _fractionDigits_ is *undefined*,
              1. Let _e_, _n_, and _f_ be integers such that _f_ ≥ 0, 10<sup>_f_</sup> ≤ _n_ < 10<sup>_f_+1</sup>, the Number value for _n_ × 10<sup>_e_-_f_</sup> is _x_, and _f_ is as small as possible. Note that the decimal representation of _n_ has _f_+1 digits, _n_ is not divisible by 10, and the least significant digit of _n_ is not necessarily uniquely determined by these criteria.
            1. Let _m_ be the String consisting of the digits of the decimal representation of _n_ (in order, with no leading zeroes).
          1. If _f_ ≠ 0, then
            1. Let _a_ be the first element of _m_, and let _b_ be the remaining _f_ elements of _m_.
            1. Let _m_ be the concatenation of the three Strings _a_, `"."`, and _b_.
          1. If _e_ = 0, then
            1. Let _c_ be `"+"`.
            1. Let _d_ be `"0"`.
          1. Else,
            1. If _e_ > 0, let _c_ be `"+"`.
            1. Else _e_ ≤ 0,
              1. Let _c_ be `"-"`.
              1. Let _e_ be -_e_.
            1. Let _d_ be the String consisting of the digits of the decimal representation of _e_ (in order, with no leading zeroes).
          1. Let _m_ be the concatenation of the four Strings _m_, `"e"`, _c_, and _d_.
          1. Return the concatenation of the Strings _s_ and _m_.