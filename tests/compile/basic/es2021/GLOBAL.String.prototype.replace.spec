          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. If _searchValue_ is neither *undefined* nor *null*, then
            1. Let _replacer_ be ? GetMethod(_searchValue_, @@replace).
            1. If _replacer_ is not *undefined*, then
              1. Return ? Call(_replacer_, _searchValue_, « _O_, _replaceValue_ »).
          1. Let _string_ be ? ToString(_O_).
          1. Let _searchString_ be ? ToString(_searchValue_).
          1. Let _functionalReplace_ be IsCallable(_replaceValue_).
          1. If _functionalReplace_ is *false*, then
            1. Set _replaceValue_ to ? ToString(_replaceValue_).
          1. Let _searchLength_ be the length of _searchString_.
          1. Let _position_ be ! StringIndexOf(_string_, _searchString_, 0).
          1. If _position_ is -1, return _string_.
          1. Let _preserved_ be the substring of _string_ from 0 to _position_.
          1. If _functionalReplace_ is *true*, then
            1. Let _replacement_ be ? ToString(? Call(_replaceValue_, *undefined*, « _searchString_, 𝔽(_position_), _string_ »)).
          1. Else,
            1. Assert: Type(_replaceValue_) is String.
            1. Let _captures_ be a new empty List.
            1. Let _replacement_ be ! GetSubstitution(_searchString_, _string_, _position_, _captures_, *undefined*, _replaceValue_).
          1. Return the string-concatenation of _preserved_, _replacement_, and the substring of _string_ from _position_ + _searchLength_.