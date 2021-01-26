          1. Evaluate the first |ClassAtom| to obtain a CharSet _A_.
          1. Evaluate the second |ClassAtom| to obtain a CharSet _B_.
          1. Evaluate |ClassRanges| to obtain a CharSet _C_.
          1. Call CharacterRange(_A_, _B_) and let _D_ be the resulting CharSet.
          1. Return the union of CharSets _D_ and _C_.