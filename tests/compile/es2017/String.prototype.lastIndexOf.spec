          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _numPos_ be ? ToNumber(_position_). (If _position_ is *undefined*, this step produces the value *NaN*.)
          1. If _numPos_ is *NaN*, let _pos_ be *+∞*; otherwise, let _pos_ be ToInteger(_numPos_).
          1. Let _len_ be the number of elements in _S_.
          1. Let _start_ be min(max(_pos_, 0), _len_).
          1. Let _searchLen_ be the number of elements in _searchStr_.
          1. Return the largest possible nonnegative integer _k_ not larger than _start_ such that _k_+_searchLen_ is not greater than _len_, and for all nonnegative integers _j_ less than _searchLen_, the code unit at index _k_+_j_ of _S_ is the same as the code unit at index _j_ of _searchStr_; but if there is no such integer _k_, return the value -1.