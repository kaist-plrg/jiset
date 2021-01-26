          1. Let _codePoints_ be a List containing the arguments passed to this function.
          1. Let _length_ be the number of elements in _codePoints_.
          1. Let _elements_ be a new empty List.
          1. Let _nextIndex_ be 0.
          1. Repeat while _nextIndex_ < _length_
            1. Let _next_ be _codePoints_[_nextIndex_].
            1. Let _nextCP_ be ? ToNumber(_next_).
            1. If SameValue(_nextCP_, ToInteger(_nextCP_)) is *false*, throw a *RangeError* exception.
            1. If _nextCP_ < 0 or _nextCP_ > 0x10FFFF, throw a *RangeError* exception.
            1. Append the elements of the UTF16Encoding of _nextCP_ to the end of _elements_.
            1. Let _nextIndex_ be _nextIndex_ + 1.
          1. Return the String value whose elements are, in order, the elements in the List _elements_. If _length_ is 0, the empty string is returned.