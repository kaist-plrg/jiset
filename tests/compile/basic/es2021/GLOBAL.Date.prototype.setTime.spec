          1. Perform ? thisTimeValue(*this* value).
          1. Let _t_ be ? ToNumber(_time_).
          1. Let _v_ be TimeClip(_t_).
          1. Set the [[DateValue]] internal slot of this Date object to _v_.
          1. Return _v_.