            1. Let _iterator_ be ? GetIterator(_items_, _method_).
            1. Let _values_ be a new empty List.
            1. Let _next_ be *true*.
            1. Repeat, while _next_ is not *false*
              1. Set _next_ to ? IteratorStep(_iterator_).
              1. If _next_ is not *false*, then
                1. Let _nextValue_ be ? IteratorValue(_next_).
                1. Append _nextValue_ to the end of the List _values_.
            1. Return _values_.