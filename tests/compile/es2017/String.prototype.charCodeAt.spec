          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _position_ be ? ToInteger(_pos_).
          1. Let _size_ be the number of elements in _S_.
          1. If _position_ < 0 or _position_ ≥ _size_, return *NaN*.
          1. Return a value of Number type, whose value is the code unit value of the element at index _position_ in the String _S_.