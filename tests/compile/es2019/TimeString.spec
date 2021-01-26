            1. Assert: Type(_tv_) is Number.
            1. Assert: _tv_ is not *NaN*.
            1. Let _hour_ be the String representation of HourFromTime(_tv_), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
            1. Let _minute_ be the String representation of MinFromTime(_tv_), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
            1. Let _second_ be the String representation of SecFromTime(_tv_), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
            1. Return the string-concatenation of _hour_, `":"`, _minute_, `":"`, _second_, the code unit 0x0020 (SPACE), and `"GMT"`.