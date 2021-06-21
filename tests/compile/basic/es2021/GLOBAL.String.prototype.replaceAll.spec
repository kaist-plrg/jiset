          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. If _searchValue_ is neither *undefined* nor *null*, then
            1. Let _isRegExp_ be ? IsRegExp(_searchValue_).
            1. If _isRegExp_ is *true*, then
              1. Let _flags_ be ? Get(_searchValue_, *"flags"*).
              1. Perform ? RequireObjectCoercible(_flags_).
              1. If ? ToString(_flags_) does not contain *"g"*, throw a *TypeError* exception.
            1. Let _replacer_ be ? GetMethod(_searchValue_, @@replace).
            1. If _replacer_ is not *undefined*, then
              1. Return ? Call(_replacer_, _searchValue_, « _O_, _replaceValue_ »).
          1. Let _string_ be ? ToString(_O_).
          1. Let _searchString_ be ? ToString(_searchValue_).
          1. Let _functionalReplace_ be IsCallable(_replaceValue_).
          1. If _functionalReplace_ is *false*, then
            1. Set _replaceValue_ to ? ToString(_replaceValue_).
          1. Let _searchLength_ be the length of _searchString_.
          1. Let _advanceBy_ be max(1, _searchLength_).
          1. Let _matchPositions_ be a new empty List.
          1. Let _position_ be ! StringIndexOf(_string_, _searchString_, 0).
          1. Repeat, while _position_ is not -1,
            1. Append _position_ to the end of _matchPositions_.
            1. Set _position_ to ! StringIndexOf(_string_, _searchString_, _position_ + _advanceBy_).
          1. Let _endOfLastMatch_ be 0.
          1. Let _result_ be the empty String.
          1. For each element _p_ of _matchPositions_, do
            1. Let _preserved_ be the substring of _string_ from _endOfLastMatch_ to _p_.
            1. If _functionalReplace_ is *true*, then
              1. Let _replacement_ be ? ToString(? Call(_replaceValue_, *undefined*, « _searchString_, 𝔽(_p_), _string_ »)).
            1. Else,
              1. Assert: Type(_replaceValue_) is String.
              1. Let _captures_ be a new empty List.
              1. Let _replacement_ be ! GetSubstitution(_searchString_, _string_, _p_, _captures_, *undefined*, _replaceValue_).
            1. Set _result_ to the string-concatenation of _result_, _preserved_, and _replacement_.
            1. Set _endOfLastMatch_ to _p_ + _searchLength_.
          1. If _endOfLastMatch_ < the length of _string_, then
            1. Set _result_ to the string-concatenation of _result_ and the substring of _string_ from _endOfLastMatch_.
          1. Return _result_.