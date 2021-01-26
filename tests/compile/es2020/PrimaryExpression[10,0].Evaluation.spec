          1. Let _pattern_ be ! UTF16Encode(BodyText of |RegularExpressionLiteral|).
          1. Let _flags_ be ! UTF16Encode(FlagText of |RegularExpressionLiteral|).
          1. Return RegExpCreate(_pattern_, _flags_).