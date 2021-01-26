          1. Evaluate |Disjunction| with +1 as its _direction_ argument to obtain a Matcher _m_.
          1. Return an internal closure that takes two arguments, a String _str_ and an integer _index_, and performs the following steps:
            1. Assert: _index_ ≤ the length of _str_.
            1. If _Unicode_ is *true*, let _Input_ be a List consisting of the sequence of code points of _str_ interpreted as a UTF-16 encoded (<emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>) Unicode string. Otherwise, let _Input_ be a List consisting of the sequence of code units that are the elements of _str_. _Input_ will be used throughout the algorithms in <emu-xref href="#sec-pattern-semantics"></emu-xref>. Each element of _Input_ is considered to be a character.
            1. Let _InputLength_ be the number of characters contained in _Input_. This variable will be used throughout the algorithms in <emu-xref href="#sec-pattern-semantics"></emu-xref>.
            1. Let _listIndex_ be the index into _Input_ of the character that was obtained from element _index_ of _str_.
            1. Let _c_ be a Continuation that always returns its State argument as a successful MatchResult.
            1. Let _cap_ be a List of _NcapturingParens_ *undefined* values, indexed 1 through _NcapturingParens_.
            1. Let _x_ be the State (_listIndex_, _cap_).
            1. Call _m_(_x_, _c_) and return its result.