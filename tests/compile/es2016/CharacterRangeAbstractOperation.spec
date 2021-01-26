            1. If _A_ does not contain exactly one character or _B_ does not contain exactly one character, throw a *SyntaxError* exception.
            1. Let _a_ be the one character in CharSet _A_.
            1. Let _b_ be the one character in CharSet _B_.
            1. Let _i_ be the character value of character _a_.
            1. Let _j_ be the character value of character _b_.
            1. If _i_ > _j_, throw a *SyntaxError* exception.
            1. Return the set containing all characters numbered _i_ through _j_, inclusive.