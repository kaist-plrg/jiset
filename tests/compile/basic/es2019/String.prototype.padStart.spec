          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _intMaxLength_ be ? ToLength(_maxLength_).
          1. Let _stringLength_ be the length of _S_.
          1. If _intMaxLength_ is not greater than _stringLength_, return _S_.
          1. If _fillString_ is *undefined*, let _filler_ be the String value consisting solely of the code unit 0x0020 (SPACE).
          1. Else, let _filler_ be ? ToString(_fillString_).
          1. If _filler_ is the empty String, return _S_.
          1. Let _fillLen_ be _intMaxLength_ - _stringLength_.
          1. Let _truncatedStringFiller_ be the String value consisting of repeated concatenations of _filler_ truncated to length _fillLen_.
          1. Return the string-concatenation of _truncatedStringFiller_ and _S_.