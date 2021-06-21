          1. Evaluate |ClassAtomNoDash| to obtain a CharSet _A_.
          1. Evaluate |ClassAtom| to obtain a CharSet _B_.
          1. Evaluate |ClassRanges| to obtain a CharSet _C_.
          1. Let _D_ be ! CharacterRange(_A_, _B_).
          1. Return the union of _D_ and _C_.