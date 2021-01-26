        1. Let _lex_ be the running execution context's LexicalEnvironment.
        1. Repeat
          1. Let _envRec_ be _lex_'s EnvironmentRecord.
          1. Let _exists_ be _envRec_.HasThisBinding().
          1. If _exists_ is *true*, return _envRec_.
          1. Let _outer_ be the value of _lex_'s outer environment reference.
          1. Let _lex_ be _outer_.