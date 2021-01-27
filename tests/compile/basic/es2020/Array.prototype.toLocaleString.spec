          1. Let _array_ be ? ToObject(*this* value).
          1. Let _len_ be ? LengthOfArrayLike(_array_).
          1. Let _separator_ be the String value for the list-separator String appropriate for the host environment's current locale (this is derived in an implementation-defined way).
          1. Let _R_ be the empty String.
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_
            1. If _k_ > 0, then
              1. Set _R_ to the string-concatenation of _R_ and _separator_.
            1. Let _nextElement_ be ? Get(_array_, ! ToString(_k_)).
            1. If _nextElement_ is not *undefined* or *null*, then
              1. Let _S_ be ? ToString(? Invoke(_nextElement_, *"toLocaleString"*)).
              1. Set _R_ to the string-concatenation of _R_ and _S_.
            1. Set _k_ to _k_ + 1.
          1. Return _R_.