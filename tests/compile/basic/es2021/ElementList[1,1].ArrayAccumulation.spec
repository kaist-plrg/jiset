          1. If |Elision| is present, then
            1. Set _nextIndex_ to the result of performing ArrayAccumulation for |Elision| with arguments _array_ and _nextIndex_.
            1. ReturnIfAbrupt(_nextIndex_).
          1. Return the result of performing ArrayAccumulation for |SpreadElement| with arguments _array_ and _nextIndex_.