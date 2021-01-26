          1. Let _t_ be ? thisTimeValue(*this* value).
          1. If _t_ is *NaN*, let _t_ be *+0*; otherwise, let _t_ be LocalTime(_t_).
          1. Let _y_ be ? ToNumber(_year_).
          1. If _month_ is not specified, let _m_ be MonthFromTime(_t_); otherwise, let _m_ be ? ToNumber(_month_).
          1. If _date_ is not specified, let _dt_ be DateFromTime(_t_); otherwise, let _dt_ be ? ToNumber(_date_).
          1. Let _newDate_ be MakeDate(MakeDay(_y_, _m_, _dt_), TimeWithinDay(_t_)).
          1. Let _u_ be TimeClip(UTC(_newDate_)).
          1. Set the [[DateValue]] internal slot of this Date object to _u_.
          1. Return _u_.