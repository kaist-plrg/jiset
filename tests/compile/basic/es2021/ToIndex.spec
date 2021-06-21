        1. If _value_ is *undefined*, then
          1. Return 0.
        1. Else,
          1. Let _integerIndex_ be 𝔽(? ToIntegerOrInfinity(_value_)).
          1. If _integerIndex_ < *+0*<sub>𝔽</sub>, throw a *RangeError* exception.
          1. Let _index_ be ! ToLength(_integerIndex_).
          1. If ! SameValue(_integerIndex_, _index_) is *false*, throw a *RangeError* exception.
          1. Return ℝ(_index_).