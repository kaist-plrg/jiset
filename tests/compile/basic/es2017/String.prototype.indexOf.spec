          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _pos_ be ? ToInteger(_position_). (If _position_ is *undefined*, this step produces the value 0.)
          1. Let _len_ be the number of elements in _S_.
          1. Let _start_ be min(max(_pos_, 0), _len_).
          1. Let _searchLen_ be the number of elements in _searchStr_.
          1. Return the smallest possible integer _k_ not smaller than _start_ such that _k_+_searchLen_ is not greater than _len_, and for all nonnegative integers _j_ less than _searchLen_, the code unit at index _k_+_j_ of _S_ is the same as the code unit at index _j_ of _searchStr_; but if there is no such integer _k_, return the value -1.