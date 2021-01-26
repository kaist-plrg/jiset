          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. Let _relativeTarget_ be ? ToInteger(_target_).
          1. If _relativeTarget_ < 0, let _to_ be max((_len_ + _relativeTarget_), 0); else let _to_ be min(_relativeTarget_, _len_).
          1. Let _relativeStart_ be ? ToInteger(_start_).
          1. If _relativeStart_ < 0, let _from_ be max((_len_ + _relativeStart_), 0); else let _from_ be min(_relativeStart_, _len_).
          1. If _end_ is *undefined*, let _relativeEnd_ be _len_; else let _relativeEnd_ be ? ToInteger(_end_).
          1. If _relativeEnd_ < 0, let _final_ be max((_len_ + _relativeEnd_), 0); else let _final_ be min(_relativeEnd_, _len_).
          1. Let _count_ be min(_final_ - _from_, _len_ - _to_).
          1. If _from_ < _to_ and _to_ < _from_ + _count_, then
            1. Let _direction_ be -1.
            1. Set _from_ to _from_ + _count_ - 1.
            1. Set _to_ to _to_ + _count_ - 1.
          1. Else,
            1. Let _direction_ be 1.
          1. Repeat, while _count_ > 0
            1. Let _fromKey_ be ! ToString(_from_).
            1. Let _toKey_ be ! ToString(_to_).
            1. Let _fromPresent_ be ? HasProperty(_O_, _fromKey_).
            1. If _fromPresent_ is *true*, then
              1. Let _fromVal_ be ? Get(_O_, _fromKey_).
              1. Perform ? Set(_O_, _toKey_, _fromVal_, *true*).
            1. Else _fromPresent_ is *false*,
              1. Perform ? DeletePropertyOrThrow(_O_, _toKey_).
            1. Set _from_ to _from_ + _direction_.
            1. Set _to_ to _to_ + _direction_.
            1. Decrease _count_ by 1.
          1. Return _O_.