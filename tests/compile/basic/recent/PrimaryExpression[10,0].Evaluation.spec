          1. Let _pattern_ be ! CodePointsToString(BodyText of |RegularExpressionLiteral|).
          1. Let _flags_ be ! CodePointsToString(FlagText of |RegularExpressionLiteral|).
          1. Return RegExpCreate(_pattern_, _flags_).