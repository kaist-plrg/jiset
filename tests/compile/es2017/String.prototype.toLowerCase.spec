          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _cpList_ be a List containing in order the code points as defined in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref> of _S_, starting at the first element of _S_.
          1. For each code point _c_ in _cpList_, if the Unicode Character Database provides a language insensitive lower case equivalent of _c_, then replace _c_ in _cpList_ with that equivalent code point(s).
          1. Let _cuList_ be a new empty List.
          1. For each code point _c_ in _cpList_, in order, append to _cuList_ the elements of the UTF16Encoding of _c_.
          1. Let _L_ be a String whose elements are, in order, the elements of _cuList_.
          1. Return _L_.