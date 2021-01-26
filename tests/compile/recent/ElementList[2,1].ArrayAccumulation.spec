          1. Set _nextIndex_ to the result of performing ArrayAccumulation for |ElementList| with arguments _array_ and _nextIndex_.
          1. ReturnIfAbrupt(_nextIndex_).
          1. If |Elision| is present, then
            1. Set _nextIndex_ to the result of performing ArrayAccumulation for |Elision| with arguments _array_ and _nextIndex_.
            1. ReturnIfAbrupt(_nextIndex_).
          1. Let _initResult_ be the result of evaluating |AssignmentExpression|.
          1. Let _initValue_ be ? GetValue(_initResult_).
          1. Let _created_ be ! CreateDataPropertyOrThrow(_array_, ! ToString(𝔽(_nextIndex_)), _initValue_).
          1. Return _nextIndex_ + 1.