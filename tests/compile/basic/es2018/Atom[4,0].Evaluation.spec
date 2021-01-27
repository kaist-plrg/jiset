          1. Evaluate |Disjunction| with argument _direction_ to obtain a Matcher _m_.
          1. Let _parenIndex_ be the number of left-capturing parentheses in the entire regular expression that occur to the left of this |Atom|. This is the total number of <emu-grammar>Atom :: `(` GroupSpecifier Disjunction `)`</emu-grammar> Parse Nodes prior to or enclosing this |Atom|.
          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps:
            1. Let _d_ be an internal Continuation closure that takes one State argument _y_ and performs the following steps:
              1. Let _cap_ be a copy of _y_'s _captures_ List.
              1. Let _xe_ be _x_'s _endIndex_.
              1. Let _ye_ be _y_'s _endIndex_.
              1. If _direction_ is equal to +1, then
                1. Assert: _xe_ ≤ _ye_.
                1. Let _s_ be a new List whose elements are the characters of _Input_ at indices _xe_ (inclusive) through _ye_ (exclusive).
              1. Else,
                1. Assert: _direction_ is equal to -1.
                1. Assert: _ye_ ≤ _xe_.
                1. Let _s_ be a new List whose elements are the characters of _Input_ at indices _ye_ (inclusive) through _xe_ (exclusive).
              1. Set _cap_[_parenIndex_+1] to _s_.
              1. Let _z_ be the State (_ye_, _cap_).
              1. Call _c_(_z_) and return its result.
            1. Call _m_(_x_, _d_) and return its result.