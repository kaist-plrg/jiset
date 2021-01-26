          1. Let _result_ be the empty String.
          1. For each element _next_ of _codePoints_, do
            1. Let _nextCP_ be ? ToNumber(_next_).
            1. If ! IsIntegralNumber(_nextCP_) is *false*, throw a *RangeError* exception.
            1. If ℝ(_nextCP_) < 0 or ℝ(_nextCP_) > 0x10FFFF, throw a *RangeError* exception.
            1. Set _result_ to the string-concatenation of _result_ and ! UTF16EncodeCodePoint(ℝ(_nextCP_)).
          1. Assert: If _codePoints_ is empty, then _result_ is the empty String.
          1. Return _result_.