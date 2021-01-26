          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If _len_ is zero, then
            1. Perform ? Set(_O_, `"length"`, 0, *true*).
            1. Return *undefined*.
          1. Let _first_ be ? Get(_O_, `"0"`).
          1. Let _k_ be 1.
          1. Repeat, while _k_ < _len_
            1. Let _from_ be ! ToString(_k_).
            1. Let _to_ be ! ToString(_k_-1).
            1. Let _fromPresent_ be ? HasProperty(_O_, _from_).
            1. If _fromPresent_ is *true*, then
              1. Let _fromVal_ be ? Get(_O_, _from_).
              1. Perform ? Set(_O_, _to_, _fromVal_, *true*).
            1. Else _fromPresent_ is *false*,
              1. Perform ? DeletePropertyOrThrow(_O_, _to_).
            1. Increase _k_ by 1.
          1. Perform ? DeletePropertyOrThrow(_O_, ! ToString(_len_-1)).
          1. Perform ? Set(_O_, `"length"`, _len_-1, *true*).
          1. Return _first_.