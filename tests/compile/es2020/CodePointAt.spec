        1. Let _size_ be the length of _string_.
        1. Assert: _position_ ≥ 0 and _position_ < _size_.
        1. Let _first_ be the code unit at index _position_ within _string_.
        1. Let _cp_ be the code point whose numeric value is that of _first_.
        1. If _first_ is not a <emu-xref href="#leading-surrogate"></emu-xref> or <emu-xref href="#trailing-surrogate"></emu-xref>, then
          1. Return the Record { [[CodePoint]]: _cp_, [[CodeUnitCount]]: 1, [[IsUnpairedSurrogate]]: *false* }.
        1. If _first_ is a <emu-xref href="#trailing-surrogate"></emu-xref> or _position_ + 1 = _size_, then
          1. Return the Record { [[CodePoint]]: _cp_, [[CodeUnitCount]]: 1, [[IsUnpairedSurrogate]]: *true* }.
        1. Let _second_ be the code unit at index _position_ + 1 within _string_.
        1. If _second_ is not a <emu-xref href="#trailing-surrogate"></emu-xref>, then
          1. Return the Record { [[CodePoint]]: _cp_, [[CodeUnitCount]]: 1, [[IsUnpairedSurrogate]]: *true* }.
        1. Set _cp_ to ! UTF16DecodeSurrogatePair(_first_, _second_).
        1. Return the Record { [[CodePoint]]: _cp_, [[CodeUnitCount]]: 2, [[IsUnpairedSurrogate]]: *false* }.