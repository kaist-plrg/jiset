        1. Let _codePoints_ be a new empty List.
        1. Let _size_ be the length of _string_.
        1. Let _position_ be 0.
        1. Repeat, while _position_ < _size_,
          1. Let _cp_ be ! CodePointAt(_string_, _position_).
          1. Append _cp_.[[CodePoint]] to _codePoints_.
          1. Set _position_ to _position_ + _cp_.[[CodeUnitCount]].
        1. Return _codePoints_.