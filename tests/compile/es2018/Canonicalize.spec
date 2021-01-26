            1. If _IgnoreCase_ is *false*, return _ch_.
            1. If _Unicode_ is *true*, then
              1. If the file CaseFolding.txt of the Unicode Character Database provides a simple or common case folding mapping for _ch_, return the result of applying that mapping to _ch_.
              1. Return _ch_.
            1. Else,
              1. Assert: _ch_ is a UTF-16 code unit.
              1. Let _s_ be the String value consisting of the single code unit _ch_.
              1. Let _u_ be the same result produced as if by performing the algorithm for `String.prototype.toUpperCase` using _s_ as the *this* value.
              1. Assert: _u_ is a String value.
              1. If _u_ does not consist of a single code unit, return _ch_.
              1. Let _cu_ be _u_'s single code unit element.
              1. If the numeric value of _ch_ ≥ 128 and the numeric value of _cu_ < 128, return _ch_.
              1. Return _cu_.