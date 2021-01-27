          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If _len_ is zero, then
            1. Perform ? Set(_O_, `"length"`, 0, *true*).
            1. Return *undefined*.
          1. Else _len_ > 0,
            1. Let _newLen_ be _len_-1.
            1. Let _indx_ be ! ToString(_newLen_).
            1. Let _element_ be ? Get(_O_, _indx_).
            1. Perform ? DeletePropertyOrThrow(_O_, _indx_).
            1. Perform ? Set(_O_, `"length"`, _newLen_, *true*).
            1. Return _element_.