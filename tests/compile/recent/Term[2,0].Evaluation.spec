          1. Evaluate |Atom| with argument _direction_ to obtain a Matcher _m_.
          1. Evaluate |Quantifier| to obtain the three results: a non-negative integer _min_, a non-negative integer (or +∞) _max_, and Boolean _greedy_.
          1. Assert: _min_ ≤ _max_.
          1. Let _parenIndex_ be the number of left-capturing parentheses in the entire regular expression that occur to the left of this |Term|. This is the total number of <emu-grammar>Atom :: `(` GroupSpecifier Disjunction `)`</emu-grammar> Parse Nodes prior to or enclosing this |Term|.
          1. Let _parenCount_ be the number of left-capturing parentheses in |Atom|. This is the total number of <emu-grammar>Atom :: `(` GroupSpecifier Disjunction `)`</emu-grammar> Parse Nodes enclosed by |Atom|.
          1. Return a new Matcher with parameters (_x_, _c_) that captures _m_, _min_, _max_, _greedy_, _parenIndex_, and _parenCount_ and performs the following steps when called:
            1. Assert: _x_ is a State.
            1. Assert: _c_ is a Continuation.
            1. Return ! RepeatMatcher(_m_, _min_, _max_, _greedy_, _x_, _c_, _parenIndex_, _parenCount_).