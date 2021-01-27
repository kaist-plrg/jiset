          1. Let _postIndex_ be the result of performing ArrayAccumulation for |ElementList| with arguments _array_ and _nextIndex_.
          1. ReturnIfAbrupt(_postIndex_).
          1. Let _padding_ be the ElisionWidth of |Elision|; if |Elision| is not present, use the numeric value zero.
          1. Let _initResult_ be the result of evaluating |AssignmentExpression|.
          1. Let _initValue_ be ? GetValue(_initResult_).
          1. Let _created_ be CreateDataProperty(_array_, ToString(ToUint32(_postIndex_ + _padding_)), _initValue_).
          1. Assert: _created_ is *true*.
          1. Return _postIndex_ + _padding_ + 1.