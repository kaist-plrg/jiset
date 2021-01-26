            1. Assert: Type(_tv_) is Number.
            1. Assert: _tv_ is not *NaN*.
            1. Let _offset_ be LocalTZA(_tv_, *true*).
            1. If _offset_ ≥ 0, let _offsetSign_ be *"+"*; otherwise, let _offsetSign_ be *"-"*.
            1. Let _offsetMin_ be the String representation of MinFromTime(abs(_offset_)), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
            1. Let _offsetHour_ be the String representation of HourFromTime(abs(_offset_)), formatted as a two-digit decimal number, padded to the left with a zero if necessary.
            1. Let _tzName_ be an implementation-defined string that is either the empty String or the string-concatenation of the code unit 0x0020 (SPACE), the code unit 0x0028 (LEFT PARENTHESIS), an implementation-dependent timezone name, and the code unit 0x0029 (RIGHT PARENTHESIS).
            1. Return the string-concatenation of _offsetSign_, _offsetHour_, _offsetMin_, and _tzName_.