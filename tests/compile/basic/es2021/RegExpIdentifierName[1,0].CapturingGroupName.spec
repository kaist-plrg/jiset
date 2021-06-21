          1. Let _idText_ be the source text matched by |RegExpIdentifierName|.
          1. Let _idTextUnescaped_ be the result of replacing any occurrences of `\\` |RegExpUnicodeEscapeSequence| in _idText_ with the code point represented by the |RegExpUnicodeEscapeSequence|.
          1. Return ! CodePointsToString(_idTextUnescaped_).