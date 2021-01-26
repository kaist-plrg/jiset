          1. Let _pattern_ be the String value consisting of the UTF16Encoding of each code point of BodyText of |RegularExpressionLiteral|.
          1. Let _flags_ be the String value consisting of the UTF16Encoding of each code point of FlagText of |RegularExpressionLiteral|.
          1. Return RegExpCreate(_pattern_, _flags_).