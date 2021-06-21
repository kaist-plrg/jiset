          1. Let _length_ be the number of elements in _codeUnits_.
          1. Let _elements_ be a new empty List.
          1. For each element _next_ of _codeUnits_, do
            1. Let _nextCU_ be ℝ(? ToUint16(_next_)).
            1. Append _nextCU_ to the end of _elements_.
          1. Return the String value whose code units are the elements in the List _elements_. If _codeUnits_ is empty, the empty String is returned.