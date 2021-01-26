          1. Let _O_ be this Date object.
          1. If _O_ does not have a [[DateValue]] internal slot, then
            1. Let _tv_ be *NaN*.
          1. Else,
            1. Let _tv_ be thisTimeValue(_O_).
          1. Return ToDateString(_tv_).