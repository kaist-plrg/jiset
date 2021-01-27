        1. Let _envRec_ be _env_'s EnvironmentRecord.
        1. Assert: _envRec_ is a global Environment Record.
        1. Let _lexNames_ be the LexicallyDeclaredNames of _script_.
        1. Let _varNames_ be the VarDeclaredNames of _script_.
        1. For each _name_ in _lexNames_, do
          1. If _envRec_.HasVarDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
          1. If _envRec_.HasLexicalDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
          1. Let _hasRestrictedGlobal_ be ? _envRec_.HasRestrictedGlobalProperty(_name_).
          1. If _hasRestrictedGlobal_ is *true*, throw a *SyntaxError* exception.
        1. For each _name_ in _varNames_, do
          1. If _envRec_.HasLexicalDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
        1. Let _varDeclarations_ be the VarScopedDeclarations of _script_.
        1. Let _functionsToInitialize_ be a new empty List.
        1. Let _declaredFunctionNames_ be a new empty List.
        1. For each _d_ in _varDeclarations_, in reverse list order, do
          1. If _d_ is neither a |VariableDeclaration| nor a |ForBinding| nor a |BindingIdentifier|, then
            1. Assert: _d_ is either a |FunctionDeclaration|, a |GeneratorDeclaration|, or an |AsyncFunctionDeclaration|.
            1. NOTE: If there are multiple function declarations for the same name, the last declaration is used.
            1. Let _fn_ be the sole element of the BoundNames of _d_.
            1. If _fn_ is not an element of _declaredFunctionNames_, then
              1. Let _fnDefinable_ be ? _envRec_.CanDeclareGlobalFunction(_fn_).
              1. If _fnDefinable_ is *false*, throw a *TypeError* exception.
              1. Append _fn_ to _declaredFunctionNames_.
              1. Insert _d_ as the first element of _functionsToInitialize_.
        1. Let _declaredVarNames_ be a new empty List.
        1. For each _d_ in _varDeclarations_, do
          1. If _d_ is a |VariableDeclaration|, a |ForBinding|, or a |BindingIdentifier|, then
            1. For each String _vn_ in the BoundNames of _d_, do
              1. If _vn_ is not an element of _declaredFunctionNames_, then
                1. Let _vnDefinable_ be ? _envRec_.CanDeclareGlobalVar(_vn_).
                1. If _vnDefinable_ is *false*, throw a *TypeError* exception.
                1. If _vn_ is not an element of _declaredVarNames_, then
                  1. Append _vn_ to _declaredVarNames_.
        1. NOTE: No abnormal terminations occur after this algorithm step if the global object is an ordinary object. However, if the global object is a Proxy exotic object it may exhibit behaviours that cause abnormal terminations in some of the following steps.
        1. NOTE: Annex <emu-xref href="#sec-web-compat-globaldeclarationinstantiation"></emu-xref> adds additional steps at this point.
        1. Let _lexDeclarations_ be the LexicallyScopedDeclarations of _script_.
        1. For each element _d_ in _lexDeclarations_, do
          1. NOTE: Lexically declared names are only instantiated here but not initialized.
          1. For each element _dn_ of the BoundNames of _d_, do
            1. If IsConstantDeclaration of _d_ is *true*, then
              1. Perform ? _envRec_.CreateImmutableBinding(_dn_, *true*).
            1. Else,
              1. Perform ? _envRec_.CreateMutableBinding(_dn_, *false*).
        1. For each Parse Node _f_ in _functionsToInitialize_, do
          1. Let _fn_ be the sole element of the BoundNames of _f_.
          1. Let _fo_ be the result of performing InstantiateFunctionObject for _f_ with argument _env_.
          1. Perform ? _envRec_.CreateGlobalFunctionBinding(_fn_, _fo_, *false*).
        1. For each String _vn_ in _declaredVarNames_, in list order, do
          1. Perform ? _envRec_.CreateGlobalVarBinding(_vn_, *false*).
        1. Return NormalCompletion(~empty~).