          1. Let _product_ be the String value consisting solely of the code unit 0x0022 (QUOTATION MARK).
          1. For each code unit _C_ in _value_, do
            1. If the numeric value of _C_ is listed in the Code Unit Value column of <emu-xref href="#table-json-single-character-escapes"></emu-xref>, then
              1. Set _product_ to the string-concatenation of _product_ and the Escape Sequence for _C_ as specified in <emu-xref href="#table-json-single-character-escapes"></emu-xref>.
            1. Else if _C_ has a numeric value less than 0x0020 (SPACE), then
              1. Set _product_ to the string-concatenation of _product_ and UnicodeEscape(_C_).
            1. Else,
              1. Set _product_ to the string-concatenation of _product_ and _C_.
          1. Set _product_ to the string-concatenation of _product_ and the code unit 0x0022 (QUOTATION MARK).
          1. Return _product_.