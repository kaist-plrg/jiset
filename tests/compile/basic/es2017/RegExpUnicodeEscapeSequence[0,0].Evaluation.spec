          1. Let _lead_ be the result of evaluating |LeadSurrogate|.
          1. Let _trail_ be the result of evaluating |TrailSurrogate|.
          1. Let _cp_ be UTF16Decode(_lead_, _trail_).
          1. Return the character whose character value is _cp_.