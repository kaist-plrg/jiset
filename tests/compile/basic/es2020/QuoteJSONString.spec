          1. Let _product_ be the String value consisting solely of the code unit 0x0022 (QUOTATION MARK).
          1. For each code point _C_ in ! UTF16DecodeString(_value_), do
            1. If _C_ is listed in the “Code Point” column of <emu-xref href="#table-json-single-character-escapes"></emu-xref>, then
              1. Set _product_ to the string-concatenation of _product_ and the escape sequence for _C_ as specified in the “Escape Sequence” column of the corresponding row.
            1. Else if _C_ has a numeric value less than 0x0020 (SPACE), or if _C_ has the same numeric value as a <emu-xref href="#leading-surrogate"></emu-xref> or <emu-xref href="#trailing-surrogate"></emu-xref>, then
              1. Let _unit_ be the code unit whose numeric value is that of _C_.
              1. Set _product_ to the string-concatenation of _product_ and UnicodeEscape(_unit_).
            1. Else,
              1. Set _product_ to the string-concatenation of _product_ and the UTF16Encoding of _C_.
          1. Set _product_ to the string-concatenation of _product_ and the code unit 0x0022 (QUOTATION MARK).
          1. Return _product_.