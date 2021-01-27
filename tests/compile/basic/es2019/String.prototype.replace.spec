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
          1. Search _string_ for the first occurrence of _searchString_ and let _pos_ be the index within _string_ of the first code unit of the matched substring and let _matched_ be _searchString_. If no occurrences of _searchString_ were found, return _string_.
          1. If _functionalReplace_ is *true*, then
            1. Let _replValue_ be ? Call(_replaceValue_, *undefined*, « _matched_, _pos_, _string_ »).
            1. Let _replStr_ be ? ToString(_replValue_).
          1. Else,
            1. Let _captures_ be a new empty List.
            1. Let _replStr_ be GetSubstitution(_matched_, _string_, _pos_, _captures_, *undefined*, _replaceValue_).
          1. Let _tailPos_ be _pos_ + the number of code units in _matched_.
          1. Let _newString_ be the string-concatenation of the first _pos_ code units of _string_, _replStr_, and the trailing substring of _string_ starting at index _tailPos_. If _pos_ is 0, the first element of the concatenation will be the empty String.
          1. Return _newString_.