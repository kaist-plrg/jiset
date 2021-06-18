          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _position_ be ? ToIntegerOrInfinity(_pos_).
          1. Let _size_ be the length of _S_.
          1. If _position_ < 0 or _position_ ≥ _size_, return *NaN*.
          1. Return the Number value for the numeric value of the code unit at index _position_ within the String _S_.