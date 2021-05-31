          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _searchStr_ be ? ToString(_searchString_).
          1. Let _pos_ be ? ToIntegerOrInfinity(_position_).
          1. Assert: If _position_ is *undefined*, then _pos_ is 0.
          1. Let _len_ be the length of _S_.
          1. Let _start_ be the result of clamping _pos_ between 0 and _len_.
          1. Return 𝔽(! StringIndexOf(_S_, _searchStr_, _start_)).