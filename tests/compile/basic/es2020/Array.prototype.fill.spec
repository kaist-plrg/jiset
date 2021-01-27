          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. Let _relativeStart_ be ? ToInteger(_start_).
          1. If _relativeStart_ < 0, let _k_ be max((_len_ + _relativeStart_), 0); else let _k_ be min(_relativeStart_, _len_).
          1. If _end_ is *undefined*, let _relativeEnd_ be _len_; else let _relativeEnd_ be ? ToInteger(_end_).
          1. If _relativeEnd_ < 0, let _final_ be max((_len_ + _relativeEnd_), 0); else let _final_ be min(_relativeEnd_, _len_).
          1. Repeat, while _k_ < _final_
            1. Let _Pk_ be ! ToString(_k_).
            1. Perform ? Set(_O_, _Pk_, _value_, *true*).
            1. Set _k_ to _k_ + 1.
          1. Return _O_.