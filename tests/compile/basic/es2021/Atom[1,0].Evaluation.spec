          1. Let _A_ be the CharSet of all characters.
          1. If _DotAll_ is not *true*, then
            1. Remove from _A_ all characters corresponding to a code point on the right-hand side of the |LineTerminator| production.
          1. Return ! CharacterSetMatcher(_A_, *false*, _direction_).