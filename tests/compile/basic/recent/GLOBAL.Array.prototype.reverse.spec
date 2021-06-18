          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_O_).
          1. Let _middle_ be floor(_len_ / 2).
          1. Let _lower_ be 0.
          1. Repeat, while _lower_ ≠ _middle_,
            1. Let _upper_ be _len_ - _lower_ - 1.
            1. Let _upperP_ be ! ToString(𝔽(_upper_)).
            1. Let _lowerP_ be ! ToString(𝔽(_lower_)).
            1. Let _lowerExists_ be ? HasProperty(_O_, _lowerP_).
            1. If _lowerExists_ is *true*, then
              1. Let _lowerValue_ be ? Get(_O_, _lowerP_).
            1. Let _upperExists_ be ? HasProperty(_O_, _upperP_).
            1. If _upperExists_ is *true*, then
              1. Let _upperValue_ be ? Get(_O_, _upperP_).
            1. If _lowerExists_ is *true* and _upperExists_ is *true*, then
              1. Perform ? Set(_O_, _lowerP_, _upperValue_, *true*).
              1. Perform ? Set(_O_, _upperP_, _lowerValue_, *true*).
            1. Else if _lowerExists_ is *false* and _upperExists_ is *true*, then
              1. Perform ? Set(_O_, _lowerP_, _upperValue_, *true*).
              1. Perform ? DeletePropertyOrThrow(_O_, _upperP_).
            1. Else if _lowerExists_ is *true* and _upperExists_ is *false*, then
              1. Perform ? DeletePropertyOrThrow(_O_, _lowerP_).
              1. Perform ? Set(_O_, _upperP_, _lowerValue_, *true*).
            1. Else,
              1. Assert: _lowerExists_ and _upperExists_ are both *false*.
              1. No action is required.
            1. Set _lower_ to _lower_ + 1.
          1. Return _O_.