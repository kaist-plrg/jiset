          1. Let _substitutions_ be a List consisting of all of the arguments passed to this function, starting with the second argument. If fewer than two arguments were passed, the List is empty.
          1. Let _numberOfSubstitutions_ be the number of elements in _substitutions_.
          1. Let _cooked_ be ? ToObject(_template_).
          1. Let _raw_ be ? ToObject(? Get(_cooked_, `"raw"`)).
          1. Let _literalSegments_ be ? ToLength(? Get(_raw_, `"length"`)).
          1. If _literalSegments_ ≤ 0, return the empty string.
          1. Let _stringElements_ be a new empty List.
          1. Let _nextIndex_ be 0.
          1. Repeat,
            1. Let _nextKey_ be ! ToString(_nextIndex_).
            1. Let _nextSeg_ be ? ToString(? Get(_raw_, _nextKey_)).
            1. Append in order the code unit elements of _nextSeg_ to the end of _stringElements_.
            1. If _nextIndex_ + 1 = _literalSegments_, then
              1. Return the String value whose code units are, in order, the elements in the List _stringElements_. If _stringElements_ has no elements, the empty string is returned.
            1. If _nextIndex_ < _numberOfSubstitutions_, let _next_ be _substitutions_[_nextIndex_].
            1. Else, let _next_ be the empty String.
            1. Let _nextSub_ be ? ToString(_next_).
            1. Append in order the code unit elements of _nextSub_ to the end of _stringElements_.
            1. Let _nextIndex_ be _nextIndex_ + 1.