          1. If _year_ is not finite or _month_ is not finite or _date_ is not finite, return *NaN*.
          1. Let _y_ be 𝔽(! ToIntegerOrInfinity(_year_)).
          1. Let _m_ be 𝔽(! ToIntegerOrInfinity(_month_)).
          1. Let _dt_ be 𝔽(! ToIntegerOrInfinity(_date_)).
          1. Let _ym_ be _y_ + 𝔽(floor(ℝ(_m_) / 12)).
          1. If _ym_ is not finite, return *NaN*.
          1. Let _mn_ be 𝔽(ℝ(_m_) modulo 12).
          1. Find a finite time value _t_ such that YearFromTime(_t_) is _ym_ and MonthFromTime(_t_) is _mn_ and DateFromTime(_t_) is *1*<sub>𝔽</sub>; but if this is not possible (because some argument is out of range), return *NaN*.
          1. Return Day(_t_) + _dt_ - *1*<sub>𝔽</sub>.