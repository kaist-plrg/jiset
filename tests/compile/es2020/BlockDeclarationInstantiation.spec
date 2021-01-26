        1. Let _envRec_ be _env_'s EnvironmentRecord.
        1. Assert: _envRec_ is a declarative Environment Record.
        1. Let _declarations_ be the LexicallyScopedDeclarations of _code_.
        1. For each element _d_ in _declarations_, do
          1. For each element _dn_ of the BoundNames of _d_, do
            1. If IsConstantDeclaration of _d_ is *true*, then
              1. Perform ! _envRec_.CreateImmutableBinding(_dn_, *true*).
            1. Else,
              1. Perform ! _envRec_.CreateMutableBinding(_dn_, *false*).
          1. If _d_ is a |FunctionDeclaration|, a |GeneratorDeclaration|, an |AsyncFunctionDeclaration|, or an |AsyncGeneratorDeclaration|, then
            1. Let _fn_ be the sole element of the BoundNames of _d_.
            1. Let _fo_ be InstantiateFunctionObject of _d_ with argument _env_.
            1. Perform _envRec_.InitializeBinding(_fn_, _fo_).