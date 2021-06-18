          1. Let _t_ be ? thisTimeValue(*this* value).
          1. Let _milli_ be ? ToNumber(_ms_).
          1. Let _time_ be MakeTime(HourFromTime(_t_), MinFromTime(_t_), SecFromTime(_t_), _milli_).
          1. Let _v_ be TimeClip(MakeDate(Day(_t_), _time_)).
          1. Set the [[DateValue]] internal slot of this Date object to _v_.
          1. Return _v_.