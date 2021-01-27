          1. Let _y_ be ? ToNumber(_year_).
          1. If _month_ is present, let _m_ be ? ToNumber(_month_); else let _m_ be *+0*<sub>𝔽</sub>.
          1. If _date_ is present, let _dt_ be ? ToNumber(_date_); else let _dt_ be *1*<sub>𝔽</sub>.
          1. If _hours_ is present, let _h_ be ? ToNumber(_hours_); else let _h_ be *+0*<sub>𝔽</sub>.
          1. If _minutes_ is present, let _min_ be ? ToNumber(_minutes_); else let _min_ be *+0*<sub>𝔽</sub>.
          1. If _seconds_ is present, let _s_ be ? ToNumber(_seconds_); else let _s_ be *+0*<sub>𝔽</sub>.
          1. If _ms_ is present, let _milli_ be ? ToNumber(_ms_); else let _milli_ be *+0*<sub>𝔽</sub>.
          1. If _y_ is *NaN*, let _yr_ be *NaN*.
          1. Else,
            1. Let _yi_ be ! ToIntegerOrInfinity(_y_).
            1. If 0 ≤ _yi_ ≤ 99, let _yr_ be *1900*<sub>𝔽</sub> + 𝔽(_yi_); otherwise, let _yr_ be _y_.
          1. Return TimeClip(MakeDate(MakeDay(_yr_, _m_, _dt_), MakeTime(_h_, _min_, _s_, _milli_))).