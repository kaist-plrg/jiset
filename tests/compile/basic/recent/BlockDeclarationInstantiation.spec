        1. Assert: _env_ is a declarative Environment Record.
        1. Let _declarations_ be the LexicallyScopedDeclarations of _code_.
        1. For each element _d_ of _declarations_, do
          1. For each element _dn_ of the BoundNames of _d_, do
            1. If IsConstantDeclaration of _d_ is *true*, then
              1. Perform ! _env_.CreateImmutableBinding(_dn_, *true*).
            1. Else,
              1. [id="step-blockdeclarationinstantiation-createmutablebinding"] Perform ! _env_.CreateMutableBinding(_dn_, *false*). NOTE: This step is replaced in section <emu-xref href="#sec-web-compat-blockdeclarationinstantiation"></emu-xref>.
          1. If _d_ is a |FunctionDeclaration|, a |GeneratorDeclaration|, an |AsyncFunctionDeclaration|, or an |AsyncGeneratorDeclaration|, then
            1. Let _fn_ be the sole element of the BoundNames of _d_.
            1. Let _fo_ be InstantiateFunctionObject of _d_ with argument _env_.
            1. [id="step-blockdeclarationinstantiation-initializebinding"] Perform _env_.InitializeBinding(_fn_, _fo_). NOTE: This step is replaced in section <emu-xref href="#sec-web-compat-blockdeclarationinstantiation"></emu-xref>.