          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _position_ be ? ToInteger(_pos_).
          1. Let _size_ be the length of _S_.
          1. If _position_ < 0 or _position_ ≥ _size_, return the empty String.
          1. Return the String value of length 1, containing one code unit from _S_, namely the code unit at index _position_.