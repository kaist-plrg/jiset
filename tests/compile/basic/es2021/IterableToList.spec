        1. If _method_ is present, then
          1. Let _iteratorRecord_ be ? GetIterator(_items_, ~sync~, _method_).
        1. Else,
          1. Let _iteratorRecord_ be ? GetIterator(_items_, ~sync~).
        1. Let _values_ be a new empty List.
        1. Let _next_ be *true*.
        1. Repeat, while _next_ is not *false*,
          1. Set _next_ to ? IteratorStep(_iteratorRecord_).
          1. If _next_ is not *false*, then
            1. Let _nextValue_ be ? IteratorValue(_next_).
            1. Append _nextValue_ to the end of the List _values_.
        1. Return _values_.