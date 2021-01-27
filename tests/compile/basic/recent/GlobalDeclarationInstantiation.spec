        1. Assert: _env_ is a global Environment Record.
        1. Let _lexNames_ be the LexicallyDeclaredNames of _script_.
        1. Let _varNames_ be the VarDeclaredNames of _script_.
        1. For each element _name_ of _lexNames_, do
          1. If _env_.HasVarDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
          1. If _env_.HasLexicalDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
          1. Let _hasRestrictedGlobal_ be ? _env_.HasRestrictedGlobalProperty(_name_).
          1. If _hasRestrictedGlobal_ is *true*, throw a *SyntaxError* exception.
        1. For each element _name_ of _varNames_, do
          1. If _env_.HasLexicalDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
        1. Let _varDeclarations_ be the VarScopedDeclarations of _script_.
        1. Let _functionsToInitialize_ be a new empty List.
        1. Let _declaredFunctionNames_ be a new empty List.
        1. For each element _d_ of _varDeclarations_, in reverse List order, do
          1. If _d_ is neither a |VariableDeclaration| nor a |ForBinding| nor a |BindingIdentifier|, then
            1. Assert: _d_ is either a |FunctionDeclaration|, a |GeneratorDeclaration|, an |AsyncFunctionDeclaration|, or an |AsyncGeneratorDeclaration|.
            1. NOTE: If there are multiple function declarations for the same name, the last declaration is used.
            1. Let _fn_ be the sole element of the BoundNames of _d_.
            1. If _fn_ is not an element of _declaredFunctionNames_, then
              1. Let _fnDefinable_ be ? _env_.CanDeclareGlobalFunction(_fn_).
              1. If _fnDefinable_ is *false*, throw a *TypeError* exception.
              1. Append _fn_ to _declaredFunctionNames_.
              1. Insert _d_ as the first element of _functionsToInitialize_.
        1. Let _declaredVarNames_ be a new empty List.
        1. For each element _d_ of _varDeclarations_, do
          1. If _d_ is a |VariableDeclaration|, a |ForBinding|, or a |BindingIdentifier|, then
            1. For each String _vn_ of the BoundNames of _d_, do
              1. If _vn_ is not an element of _declaredFunctionNames_, then
                1. Let _vnDefinable_ be ? _env_.CanDeclareGlobalVar(_vn_).
                1. If _vnDefinable_ is *false*, throw a *TypeError* exception.
                1. If _vn_ is not an element of _declaredVarNames_, then
                  1. Append _vn_ to _declaredVarNames_.
        1. NOTE: No abnormal terminations occur after this algorithm step if the global object is an ordinary object. However, if the global object is a Proxy exotic object it may exhibit behaviours that cause abnormal terminations in some of the following steps.
        1. [id="step-globaldeclarationinstantiation-web-compat-insertion-point"] NOTE: Annex <emu-xref href="#sec-web-compat-globaldeclarationinstantiation"></emu-xref> adds additional steps at this point.
        1. Let _lexDeclarations_ be the LexicallyScopedDeclarations of _script_.
        1. For each element _d_ of _lexDeclarations_, do
          1. NOTE: Lexically declared names are only instantiated here but not initialized.
          1. For each element _dn_ of the BoundNames of _d_, do
            1. If IsConstantDeclaration of _d_ is *true*, then
              1. Perform ? _env_.CreateImmutableBinding(_dn_, *true*).
            1. Else,
              1. Perform ? _env_.CreateMutableBinding(_dn_, *false*).
        1. For each Parse Node _f_ of _functionsToInitialize_, do
          1. Let _fn_ be the sole element of the BoundNames of _f_.
          1. Let _fo_ be InstantiateFunctionObject of _f_ with argument _env_.
          1. Perform ? _env_.CreateGlobalFunctionBinding(_fn_, _fo_, *false*).
        1. For each String _vn_ of _declaredVarNames_, do
          1. Perform ? _env_.CreateGlobalVarBinding(_vn_, *false*).
        1. Return NormalCompletion(~empty~).