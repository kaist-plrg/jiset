        1. Let _inputString_ be ? ToString(_string_).
        1. Let _S_ be a newly created substring of _inputString_ consisting of the first code unit that is not a |StrWhiteSpaceChar| and all code units following that code unit. (In other words, remove leading white space.) If _inputString_ does not contain any such code unit, let _S_ be the empty string.
        1. Let _sign_ be 1.
        1. If _S_ is not empty and the first code unit of _S_ is the code unit 0x002D (HYPHEN-MINUS), let _sign_ be -1.
        1. If _S_ is not empty and the first code unit of _S_ is the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS), remove the first code unit from _S_.
        1. Let _R_ be ? ToInt32(_radix_).
        1. Let _stripPrefix_ be *true*.
        1. If _R_ ≠ 0, then
          1. If _R_ < 2 or _R_ > 36, return *NaN*.
          1. If _R_ ≠ 16, let _stripPrefix_ be *false*.
        1. Else _R_ = 0,
          1. Let _R_ be 10.
        1. If _stripPrefix_ is *true*, then
          1. If the length of _S_ is at least 2 and the first two code units of _S_ are either `"0x"` or `"0X"`, remove the first two code units from _S_ and let _R_ be 16.
        1. If _S_ contains a code unit that is not a radix-_R_ digit, let _Z_ be the substring of _S_ consisting of all code units before the first such code unit; otherwise, let _Z_ be _S_.
        1. If _Z_ is empty, return *NaN*.
        1. Let _mathInt_ be the mathematical integer value that is represented by _Z_ in radix-_R_ notation, using the letters <b>A</b>-<b>Z</b> and <b>a</b>-<b>z</b> for digits with values 10 through 35. (However, if _R_ is 10 and _Z_ contains more than 20 significant digits, every significant digit after the 20th may be replaced by a 0 digit, at the option of the implementation; and if _R_ is not 2, 4, 8, 10, 16, or 32, then _mathInt_ may be an implementation-dependent approximation to the mathematical integer value that is represented by _Z_ in radix-_R_ notation.)
        1. If _mathInt_ = 0, then
          1. If _sign_ = -1, return *-0*.
          1. Return *+0*.
        1. Let _number_ be the Number value for _mathInt_.
        1. Return _sign_ × _number_.