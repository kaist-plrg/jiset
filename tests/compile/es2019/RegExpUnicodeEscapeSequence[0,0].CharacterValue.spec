          1. Let _lead_ be the CharacterValue of |LeadSurrogate|.
          1. Let _trail_ be the CharacterValue of |TrailSurrogate|.
          1. Let _cp_ be UTF16Decode(_lead_, _trail_).
          1. Return the code point value of _cp_.