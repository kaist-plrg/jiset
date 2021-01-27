          1. Evaluate |DecimalEscape| to obtain an EscapeValue _E_.
          1. If _E_ is a character, then
            1. Let _ch_ be _E_'s character.
            1. Let _A_ be a one-element CharSet containing the character _ch_.
            1. Call CharacterSetMatcher(_A_, *false*) and return its Matcher result.
          1. Assert: _E_ must be an integer.
          1. Let _n_ be that integer.
          1. If _n_=0 or _n_>_NcapturingParens_, throw a *SyntaxError* exception.
          1. Return an internal Matcher closure that takes two arguments, a State _x_ and a Continuation _c_, and performs the following steps:
            1. Let _cap_ be _x_'s _captures_ List.
            1. Let _s_ be _cap_[_n_].
            1. If _s_ is *undefined*, return _c_(_x_).
            1. Let _e_ be _x_'s _endIndex_.
            1. Let _len_ be _s_'s length.
            1. Let _f_ be _e_+_len_.
            1. If _f_>_InputLength_, return ~failure~.
            1. If there exists an integer _i_ between 0 (inclusive) and _len_ (exclusive) such that Canonicalize(_s_[_i_]) is not the same character value as Canonicalize(_Input_[_e_+_i_]), return ~failure~.
            1. Let _y_ be the State (_f_, _cap_).
            1. Call _c_(_y_) and return its result.