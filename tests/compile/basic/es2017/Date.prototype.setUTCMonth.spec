          1. Let _t_ be ? thisTimeValue(*this* value).
          1. Let _m_ be ? ToNumber(_month_).
          1. If _date_ is not specified, let _dt_ be DateFromTime(_t_).
          1. Else,
            1. Let _dt_ be ? ToNumber(_date_).
          1. Let _newDate_ be MakeDate(MakeDay(YearFromTime(_t_), _m_, _dt_), TimeWithinDay(_t_)).
          1. Let _v_ be TimeClip(_newDate_).
          1. Set the [[DateValue]] internal slot of this Date object to _v_.
          1. Return _v_.