          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _isRegExp_ be ? IsRegExp(_searchString_).
          1. If _isRegExp_ is *true*, throw a *TypeError* exception.
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _len_ be the length of _S_.
          1. If _position_ is *undefined*, let _pos_ be 0; else let _pos_ be ? ToIntegerOrInfinity(_position_).
          1. Let _start_ be the result of clamping _pos_ between 0 and _len_.
          1. Let _searchLength_ be the length of _searchStr_.
          1. If _searchLength_ = 0, return *true*.
          1. Let _end_ be _start_ + _searchLength_.
          1. If _end_ > _len_, return *false*.
          1. Let _substring_ be the substring of _S_ from _start_ to _end_.
          1. Return ! SameValueNonNumeric(_substring_, _searchStr_).