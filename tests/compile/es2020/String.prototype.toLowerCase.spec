          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _sText_ be ! UTF16DecodeString(_S_).
          1. Let _lowerText_ be the result of toLowercase(_sText_), according to the Unicode Default Case Conversion algorithm.
          1. Let _L_ be ! UTF16Encode(_lowerText_).
          1. Return _L_.