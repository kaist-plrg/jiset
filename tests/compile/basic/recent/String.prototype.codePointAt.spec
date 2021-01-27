          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _position_ be ? ToIntegerOrInfinity(_pos_).
          1. Let _size_ be the length of _S_.
          1. If _position_ < 0 or _position_ ≥ _size_, return *undefined*.
          1. Let _cp_ be ! CodePointAt(_S_, _position_).
          1. Return 𝔽(_cp_.[[CodePoint]]).