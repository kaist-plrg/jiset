            1. Assert: Type(_S_) is String.
            1. Assert: 0 ≤ _index_ ≤ 2<sup>53</sup> - 1 and ! IsInteger(_index_) is *true*.
            1. Assert: Type(_unicode_) is Boolean.
            1. If _unicode_ is *false*, return _index_ + 1.
            1. Let _length_ be the number of code units in _S_.
            1. If _index_ + 1 ≥ _length_, return _index_ + 1.
            1. Let _cp_ be ! CodePointAt(_S_, _index_).
            1. Return _index_ + _cp_.[[CodeUnitCount]].