          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _cpList_ be a List containing in order the code points as defined in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref> of _S_, starting at the first element of _S_.
          1. Let _cuList_ be a List where the elements are the result of toLowercase(_cpList_), according to the Unicode Default Case Conversion algorithm.
          1. Let _L_ be the String value whose code units are the UTF16Encoding of the code points of _cuList_.
          1. Return _L_.