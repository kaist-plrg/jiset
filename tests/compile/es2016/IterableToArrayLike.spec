            1. Let _usingIterator_ be ? GetMethod(_items_, @@iterator).
            1. If _usingIterator_ is not *undefined*, then
              1. Let _iterator_ be ? GetIterator(_items_, _usingIterator_).
              1. Let _values_ be a new empty List.
              1. Let _next_ be *true*.
              1. Repeat, while _next_ is not *false*
                1. Let _next_ be ? IteratorStep(_iterator_).
                1. If _next_ is not *false*, then
                  1. Let _nextValue_ be ? IteratorValue(_next_).
                  1. Append _nextValue_ to the end of the List _values_.
              1. Return CreateArrayFromList(_values_).
            1. NOTE: _items_ is not an Iterable so assume it is already an array-like object.
            1. Return ! ToObject(_items_).