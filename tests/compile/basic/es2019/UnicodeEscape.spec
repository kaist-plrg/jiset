          1. Let _n_ be the numeric value of _C_.
          1. Assert: _n_ ≤ 0xFFFF.
          1. Return the string-concatenation of:
            * the code unit 0x005C (REVERSE SOLIDUS)
            * `"u"`
            * the String representation of _n_, formatted as a four-digit lowercase hexadecimal number, padded to the left with zeroes if necessary