          1. Let _array_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_array_, `"length"`)).
          1. Let _separator_ be the String value for the list-separator String appropriate for the host environment's current locale (this is derived in an implementation-defined way).
          1. If _len_ is zero, return the empty String.
          1. Let _firstElement_ be ? Get(_array_, `"0"`).
          1. If _firstElement_ is *undefined* or *null*, then
            1. Let _R_ be the empty String.
          1. Else,
            1. Let _R_ be ? ToString(? Invoke(_firstElement_, `"toLocaleString"`)).
          1. Let _k_ be `1`.
          1. Repeat, while _k_ < _len_
            1. Let _S_ be a String value produced by concatenating _R_ and _separator_.
            1. Let _nextElement_ be ? Get(_array_, ! ToString(_k_)).
            1. If _nextElement_ is *undefined* or *null*, then
              1. Let _R_ be the empty String.
            1. Else,
              1. Let _R_ be ? ToString(? Invoke(_nextElement_, `"toLocaleString"`)).
            1. Let _R_ be a String value produced by concatenating _S_ and _R_.
            1. Increase _k_ by 1.
          1. Return _R_.