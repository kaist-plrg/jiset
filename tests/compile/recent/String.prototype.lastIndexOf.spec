          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _numPos_ be ? ToNumber(_position_).
          1. Assert: If _position_ is *undefined*, then _numPos_ is *NaN*.
          1. If _numPos_ is *NaN*, let _pos_ be +∞; otherwise, let _pos_ be ! ToIntegerOrInfinity(_numPos_).
          1. Let _len_ be the length of _S_.
          1. Let _start_ be the result of clamping _pos_ between 0 and _len_.
          1. Let _searchLen_ be the length of _searchStr_.
          1. Let _k_ be the largest possible non-negative integer not larger than _start_ such that _k_ + _searchLen_ ≤ _len_, and for all non-negative integers _j_ such that _j_ < _searchLen_, the code unit at index _k_ + _j_ within _S_ is the same as the code unit at index _j_ within _searchStr_; but if there is no such integer, let _k_ be -1.
          1. Return 𝔽(_k_).