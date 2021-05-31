        1. Let _inputString_ be ? ToString(_string_).
        1. Let _S_ be ! TrimString(_inputString_, ~start~).
        1. Let _sign_ be 1.
        1. If _S_ is not empty and the first code unit of _S_ is the code unit 0x002D (HYPHEN-MINUS), set _sign_ to -1.
        1. If _S_ is not empty and the first code unit of _S_ is the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS), remove the first code unit from _S_.
        1. Let _R_ be ℝ(? ToInt32(_radix_)).
        1. Let _stripPrefix_ be *true*.
        1. If _R_ ≠ 0, then
          1. If _R_ < 2 or _R_ > 36, return *NaN*.
          1. If _R_ ≠ 16, set _stripPrefix_ to *false*.
        1. Else,
          1. Set _R_ to 10.
        1. If _stripPrefix_ is *true*, then
          1. If the length of _S_ is at least 2 and the first two code units of _S_ are either *"0x"* or *"0X"*, then
            1. Remove the first two code units from _S_.
            1. Set _R_ to 16.
        1. If _S_ contains a code unit that is not a radix-_R_ digit, let _end_ be the index within _S_ of the first such code unit; otherwise, let _end_ be the length of _S_.
        1. Let _Z_ be the substring of _S_ from 0 to _end_.
        1. If _Z_ is empty, return *NaN*.
        1. Let _mathInt_ be the integer value that is represented by _Z_ in radix-_R_ notation, using the letters <b>A</b>-<b>Z</b> and <b>a</b>-<b>z</b> for digits with values 10 through 35. (However, if _R_ is 10 and _Z_ contains more than 20 significant digits, every significant digit after the 20th may be replaced by a 0 digit, at the option of the implementation; and if _R_ is not 2, 4, 8, 10, 16, or 32, then _mathInt_ may be an implementation-approximated value representing the integer value that is represented by _Z_ in radix-_R_ notation.)
        1. If _mathInt_ = 0, then
          1. If _sign_ = -1, return *-0*<sub>𝔽</sub>.
          1. Return *+0*<sub>𝔽</sub>.
        1. Return 𝔽(_sign_ × _mathInt_).