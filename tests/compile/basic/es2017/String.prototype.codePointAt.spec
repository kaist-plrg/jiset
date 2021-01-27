          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _position_ be ? ToInteger(_pos_).
          1. Let _size_ be the number of elements in _S_.
          1. If _position_ < 0 or _position_ ≥ _size_, return *undefined*.
          1. Let _first_ be the code unit value of the element at index _position_ in the String _S_.
          1. If _first_ < 0xD800 or _first_ > 0xDBFF or _position_+1 = _size_, return _first_.
          1. Let _second_ be the code unit value of the element at index _position_+1 in the String _S_.
          1. If _second_ < 0xDC00 or _second_ > 0xDFFF, return _first_.
          1. Return UTF16Decode(_first_, _second_).