          1. If _DotAll_ is *true*, then
            1. Let _A_ be the set of all characters.
          1. Otherwise, let _A_ be the set of all characters except |LineTerminator|.
          1. Call CharacterSetMatcher(_A_, *false*, _direction_) and return its Matcher result.