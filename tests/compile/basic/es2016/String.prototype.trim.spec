          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. Let _T_ be a String value that is a copy of _S_ with both leading and trailing white space removed. The definition of white space is the union of |WhiteSpace| and |LineTerminator|. When determining whether a Unicode code point is in Unicode general category “Zs”, code unit sequences are interpreted as UTF-16 encoded code point sequences as specified in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>.
          1. Return _T_.