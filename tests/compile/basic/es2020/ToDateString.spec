            1. Assert: Type(_tv_) is Number.
            1. If _tv_ is *NaN*, return *"Invalid Date"*.
            1. Let _t_ be LocalTime(_tv_).
            1. Return the string-concatenation of DateString(_t_), the code unit 0x0020 (SPACE), TimeString(_t_), and TimeZoneString(_tv_).