            1. Assert: Type(_tv_) is Number.
            1. Assert: _tv_ is not *NaN*.
            1. Let _offset_ be LocalTZA(_tv_, *true*).
            1. If _offset_ ≥ *+0*<sub>𝔽</sub>, then
              1. Let _offsetSign_ be *"+"*.
              1. Let _absOffset_ be _offset_.
            1. Else,
              1. Let _offsetSign_ be *"-"*.
              1. Let _absOffset_ be -_offset_.
            1. Let _offsetMin_ be the String representation of MinFromTime(_absOffset_), formatted as a two-digit decimal number, padded to the left with the code unit 0x0030 (DIGIT ZERO) if necessary.
            1. Let _offsetHour_ be the String representation of HourFromTime(_absOffset_), formatted as a two-digit decimal number, padded to the left with the code unit 0x0030 (DIGIT ZERO) if necessary.
            1. Let _tzName_ be an implementation-defined string that is either the empty String or the string-concatenation of the code unit 0x0020 (SPACE), the code unit 0x0028 (LEFT PARENTHESIS), an implementation-defined timezone name, and the code unit 0x0029 (RIGHT PARENTHESIS).
            1. Return the string-concatenation of _offsetSign_, _offsetHour_, _offsetMin_, and _tzName_.