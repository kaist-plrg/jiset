          1. If NewTarget is *undefined*, then
            1. Let _now_ be the time value (UTC) identifying the current time.
            1. Return ToDateString(_now_).
          1. Let _numberOfArgs_ be the number of elements in _values_.
          1. If _numberOfArgs_ = 0, then
            1. Let _dv_ be the time value (UTC) identifying the current time.
          1. Else if _numberOfArgs_ = 1, then
            1. Let _value_ be _values_[0].
            1. If Type(_value_) is Object and _value_ has a [[DateValue]] internal slot, then
              1. Let _tv_ be ! thisTimeValue(_value_).
            1. Else,
              1. Let _v_ be ? ToPrimitive(_value_).
              1. If Type(_v_) is String, then
                1. Assert: The next step never returns an abrupt completion because Type(_v_) is String.
                1. Let _tv_ be the result of parsing _v_ as a date, in exactly the same manner as for the `parse` method (<emu-xref href="#sec-date.parse"></emu-xref>).
              1. Else,
                1. Let _tv_ be ? ToNumber(_v_).
            1. Let _dv_ be TimeClip(_tv_).
          1. Else,
            1. Assert: _numberOfArgs_ ≥ 2.
            1. Let _y_ be ? ToNumber(_values_[0]).
            1. Let _m_ be ? ToNumber(_values_[1]).
            1. If _numberOfArgs_ > 2, let _dt_ be ? ToNumber(_values_[2]); else let _dt_ be *1*<sub>𝔽</sub>.
            1. If _numberOfArgs_ > 3, let _h_ be ? ToNumber(_values_[3]); else let _h_ be *+0*<sub>𝔽</sub>.
            1. If _numberOfArgs_ > 4, let _min_ be ? ToNumber(_values_[4]); else let _min_ be *+0*<sub>𝔽</sub>.
            1. If _numberOfArgs_ > 5, let _s_ be ? ToNumber(_values_[5]); else let _s_ be *+0*<sub>𝔽</sub>.
            1. If _numberOfArgs_ > 6, let _milli_ be ? ToNumber(_values_[6]); else let _milli_ be *+0*<sub>𝔽</sub>.
            1. If _y_ is *NaN*, let _yr_ be *NaN*.
            1. Else,
              1. Let _yi_ be ! ToIntegerOrInfinity(_y_).
              1. If 0 ≤ _yi_ ≤ 99, let _yr_ be *1900*<sub>𝔽</sub> + 𝔽(_yi_); otherwise, let _yr_ be _y_.
            1. Let _finalDate_ be MakeDate(MakeDay(_yr_, _m_, _dt_), MakeTime(_h_, _min_, _s_, _milli_)).
            1. Let _dv_ be TimeClip(UTC(_finalDate_)).
          1. Let _O_ be ? OrdinaryCreateFromConstructor(NewTarget, *"%Date.prototype%"*, « [[DateValue]] »).
          1. Set _O_.[[DateValue]] to _dv_.
          1. Return _O_.