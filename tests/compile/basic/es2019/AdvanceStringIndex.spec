            1. Assert: Type(_S_) is String.
            1. Assert: _index_ is an integer such that 0 ≤ _index_ ≤ 2<sup>53</sup> - 1.
            1. Assert: Type(_unicode_) is Boolean.
            1. If _unicode_ is *false*, return _index_ + 1.
            1. Let _length_ be the number of code units in _S_.
            1. If _index_ + 1 ≥ _length_, return _index_ + 1.
            1. Let _first_ be the numeric value of the code unit at index _index_ within _S_.
            1. If _first_ < 0xD800 or _first_ > 0xDBFF, return _index_ + 1.
            1. Let _second_ be the numeric value of the code unit at index _index_ + 1 within _S_.
            1. If _second_ < 0xDC00 or _second_ > 0xDFFF, return _index_ + 1.
            1. Return _index_ + 2.