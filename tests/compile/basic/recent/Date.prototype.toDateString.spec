          1. Let _O_ be this Date object.
          1. Let _tv_ be ? thisTimeValue(_O_).
          1. If _tv_ is *NaN*, return *"Invalid Date"*.
          1. Let _t_ be LocalTime(_tv_).
          1. Return DateString(_t_).