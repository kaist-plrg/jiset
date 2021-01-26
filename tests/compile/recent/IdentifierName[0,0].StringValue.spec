          1. Let _idText_ be the source text matched by |IdentifierName|.
          1. Let _idTextUnescaped_ be the result of replacing any occurrences of `\\` |UnicodeEscapeSequence| in _idText_ with the code point represented by the |UnicodeEscapeSequence|.
          1. Return ! CodePointsToString(_idTextUnescaped_).