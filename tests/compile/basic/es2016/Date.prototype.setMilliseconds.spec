          1. Let _t_ be LocalTime(? thisTimeValue(*this* value)).
          1. Let _ms_ be ? ToNumber(_ms_).
          1. Let _time_ be MakeTime(HourFromTime(_t_), MinFromTime(_t_), SecFromTime(_t_), _ms_).
          1. Let _u_ be TimeClip(UTC(MakeDate(Day(_t_), _time_))).
          1. Set the [[DateValue]] internal slot of this Date object to _u_.
          1. Return _u_.