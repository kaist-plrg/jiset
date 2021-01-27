        1. Let _result_ be the empty String.
        1. For each code point _cp_ of _text_, do
          1. Set _result_ to the string-concatenation of _result_ and ! UTF16EncodeCodePoint(_cp_).
        1. Return _result_.