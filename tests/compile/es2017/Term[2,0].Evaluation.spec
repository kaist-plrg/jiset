          1. Evaluate |Atom| to obtain a Matcher _m_.
          1. Evaluate |Quantifier| to obtain the three results: an integer _min_, an integer (or ∞) _max_, and Boolean _greedy_.
          1. If _max_ is finite and less than _min_, throw a *SyntaxError* exception.
          1. Let _parenIndex_ be the number of left-capturing parentheses in the entire regular expression that occur to the left of this |Term|. This is the total number of <emu-grammar>Atom :: `(` Disjunction `)`</emu-grammar> Parse Nodes prior to or enclosing this |Term|.
          1. Let _parenCount_ be the number of left-capturing parentheses in |Atom|. This is the total number of <emu-grammar>Atom :: `(` Disjunction `)`</emu-grammar> Parse Nodes enclosed by |Atom|.
          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps when evaluated:
            1. Call RepeatMatcher(_m_, _min_, _max_, _greedy_, _x_, _c_, _parenIndex_, _parenCount_) and return its result.