          1. Let _postIndex_ be the result of performing ArrayAccumulation for |ElementList| with arguments _array_ and _nextIndex_.
          1. ReturnIfAbrupt(_postIndex_).
          1. Let _padding_ be the ElisionWidth of |Elision|; if |Elision| is not present, use the numeric value zero.
          1. Return the result of performing ArrayAccumulation for |SpreadElement| with arguments _array_ and _postIndex_+_padding_.