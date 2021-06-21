          1. Assert: _S_ is an Object that has a [[StringData]] internal slot.
          1. Assert: IsPropertyKey(_P_) is *true*.
          1. If Type(_P_) is not String, return *undefined*.
          1. Let _index_ be ! CanonicalNumericIndexString(_P_).
          1. If _index_ is *undefined*, return *undefined*.
          1. If IsIntegralNumber(_index_) is *false*, return *undefined*.
          1. If _index_ is *-0*<sub>𝔽</sub>, return *undefined*.
          1. Let _str_ be _S_.[[StringData]].
          1. Assert: Type(_str_) is String.
          1. Let _len_ be the length of _str_.
          1. If ℝ(_index_) < 0 or _len_ ≤ ℝ(_index_), return *undefined*.
          1. Let _resultStr_ be the String value of length 1, containing one code unit from _str_, specifically the code unit at index ℝ(_index_).
          1. Return the PropertyDescriptor { [[Value]]: _resultStr_, [[Writable]]: *false*, [[Enumerable]]: *true*, [[Configurable]]: *false* }.