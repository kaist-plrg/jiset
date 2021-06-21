          1. Let _t_ be ? thisTimeValue(*this* value).
          1. Let _m_ be ? ToNumber(_min_).
          1. If _sec_ is not present, let _s_ be SecFromTime(_t_).
          1. Else,
            1. Let _s_ be ? ToNumber(_sec_).
          1. If _ms_ is not present, let _milli_ be msFromTime(_t_).
          1. Else,
            1. Let _milli_ be ? ToNumber(_ms_).
          1. Let _date_ be MakeDate(Day(_t_), MakeTime(HourFromTime(_t_), _m_, _s_, _milli_)).
          1. Let _v_ be TimeClip(_date_).
          1. Set the [[DateValue]] internal slot of this Date object to _v_.
          1. Return _v_.