          1. Let _lead_ be the CharacterValue of |HexLeadSurrogate|.
          1. Let _trail_ be the CharacterValue of |HexTrailSurrogate|.
          1. Let _cp_ be UTF16SurrogatePairToCodePoint(_lead_, _trail_).
          1. Return the code point value of _cp_.