          1. Let _codeUnits_ be a List containing the arguments passed to this function.
          1. Let _length_ be the number of elements in _codeUnits_.
          1. Let _elements_ be a new empty List.
          1. Let _nextIndex_ be 0.
          1. Repeat, while _nextIndex_ < _length_
            1. Let _next_ be _codeUnits_[_nextIndex_].
            1. Let _nextCU_ be ? ToUint16(_next_).
            1. Append _nextCU_ to the end of _elements_.
            1. Increase _nextIndex_ by 1.
          1. Return the String value whose code units are, in order, the elements in the List _elements_. If _length_ is 0, the empty string is returned.