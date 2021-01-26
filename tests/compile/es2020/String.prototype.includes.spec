          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _isRegExp_ be ? IsRegExp(_searchString_).
          1. If _isRegExp_ is *true*, throw a *TypeError* exception.
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _pos_ be ? ToInteger(_position_).
          1. Assert: If _position_ is *undefined*, then _pos_ is 0.
          1. Let _len_ be the length of _S_.
          1. Let _start_ be min(max(_pos_, 0), _len_).
          1. Let _searchLen_ be the length of _searchStr_.
          1. If there exists any integer _k_ not smaller than _start_ such that _k_ + _searchLen_ is not greater than _len_, and for all nonnegative integers _j_ less than _searchLen_, the code unit at index _k_ + _j_ within _S_ is the same as the code unit at index _j_ within _searchStr_, return *true*; but if there is no such integer _k_, return *false*.