          1. Let _t_ be ? thisTimeValue(*this* value).
          1. Let _h_ be ? ToNumber(_hour_).
          1. If _min_ is not specified, let _m_ be MinFromTime(_t_); otherwise, let _m_ be ? ToNumber(_min_).
          1. If _sec_ is not specified, let _s_ be SecFromTime(_t_); otherwise, let _s_ be ? ToNumber(_sec_).
          1. If _ms_ is not specified, let _milli_ be msFromTime(_t_); otherwise, let _milli_ be ? ToNumber(_ms_).
          1. Let _newDate_ be MakeDate(Day(_t_), MakeTime(_h_, _m_, _s_, _milli_)).
          1. Let _v_ be TimeClip(_newDate_).
          1. Set the [[DateValue]] internal slot of this Date object to _v_.
          1. Return _v_.