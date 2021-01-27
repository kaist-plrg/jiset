          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _isRegExp_ be ? IsRegExp(_searchString_).
          1. If _isRegExp_ is *true*, throw a *TypeError* exception.
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _len_ be the length of _S_.
          1. If _endPosition_ is *undefined*, let _pos_ be _len_; else let _pos_ be ? ToInteger(_endPosition_).
          1. Let _end_ be min(max(_pos_, 0), _len_).
          1. Let _searchLength_ be the length of _searchStr_.
          1. Let _start_ be _end_ - _searchLength_.
          1. If _start_ is less than 0, return *false*.
          1. If the sequence of code units of _S_ starting at _start_ of length _searchLength_ is the same as the full code unit sequence of _searchStr_, return *true*.
          1. Otherwise, return *false*.