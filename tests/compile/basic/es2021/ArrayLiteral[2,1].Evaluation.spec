          1. Let _array_ be ! ArrayCreate(0).
          1. Let _nextIndex_ be the result of performing ArrayAccumulation for |ElementList| with arguments _array_ and 0.
          1. ReturnIfAbrupt(_nextIndex_).
          1. If |Elision| is present, then
            1. Let _len_ be the result of performing ArrayAccumulation for |Elision| with arguments _array_ and _nextIndex_.
            1. ReturnIfAbrupt(_len_).
          1. Return _array_.