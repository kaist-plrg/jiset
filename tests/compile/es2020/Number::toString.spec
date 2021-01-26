            1. If _x_ is *NaN*, return the String *"NaN"*.
            1. If _x_ is *+0* or *-0*, return the String *"0"*.
            1. If _x_ is less than zero, return the string-concatenation of *"-"* and ! Number::toString(-_x_).
            1. If _x_ is *+∞*, return the String *"Infinity"*.
            1. Otherwise, let _n_, _k_, and _s_ be integers such that _k_ ≥ 1, 10<sup>_k_ - 1</sup> ≤ _s_ < 10<sup>_k_</sup>, the Number value for ℝ(_s_) × 10<sub>ℝ</sub><sup>ℝ(_n_) - ℝ(_k_)</sup> is _x_, and _k_ is as small as possible. Note that _k_ is the number of digits in the decimal representation of _s_, that _s_ is not divisible by 10<sub>ℝ</sub>, and that the least significant digit of _s_ is not necessarily uniquely determined by these criteria.
            1. If _k_ ≤ _n_ ≤ 21, return the string-concatenation of:
              * the code units of the _k_ digits of the decimal representation of _s_ (in order, with no leading zeroes)
              * _n_ - _k_ occurrences of the code unit 0x0030 (DIGIT ZERO)
            1. If 0 < _n_ ≤ 21, return the string-concatenation of:
              * the code units of the most significant _n_ digits of the decimal representation of _s_
              * the code unit 0x002E (FULL STOP)
              * the code units of the remaining _k_ - _n_ digits of the decimal representation of _s_
            1. If -6 < _n_ ≤ 0, return the string-concatenation of:
              * the code unit 0x0030 (DIGIT ZERO)
              * the code unit 0x002E (FULL STOP)
              * -_n_ occurrences of the code unit 0x0030 (DIGIT ZERO)
              * the code units of the _k_ digits of the decimal representation of _s_
            1. Otherwise, if _k_ = 1, return the string-concatenation of:
              * the code unit of the single digit of _s_
              * the code unit 0x0065 (LATIN SMALL LETTER E)
              * the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS) according to whether _n_ - 1 is positive or negative
              * the code units of the decimal representation of the integer abs(_n_ - 1) (with no leading zeroes)
            1. Return the string-concatenation of:
              * the code units of the most significant digit of the decimal representation of _s_
              * the code unit 0x002E (FULL STOP)
              * the code units of the remaining _k_ - 1 digits of the decimal representation of _s_
              * the code unit 0x0065 (LATIN SMALL LETTER E)
              * the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS) according to whether _n_ - 1 is positive or negative
              * the code units of the decimal representation of the integer abs(_n_ - 1) (with no leading zeroes)