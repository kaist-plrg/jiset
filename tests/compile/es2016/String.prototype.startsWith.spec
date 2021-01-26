          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _isRegExp_ be ? IsRegExp(_searchString_).
          1. If _isRegExp_ is *true*, throw a *TypeError* exception.
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _pos_ be ? ToInteger(_position_). (If _position_ is *undefined*, this step produces the value 0.)
          1. Let _len_ be the number of elements in _S_.
          1. Let _start_ be min(max(_pos_, 0), _len_).
          1. Let _searchLength_ be the number of elements in _searchStr_.
          1. If _searchLength_+_start_ is greater than _len_, return *false*.
          1. If the sequence of elements of _S_ starting at _start_ of length _searchLength_ is the same as the full element sequence of _searchStr_, return *true*.
          1. Otherwise, return *false*.