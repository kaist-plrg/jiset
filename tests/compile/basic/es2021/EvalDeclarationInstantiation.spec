          1. Let _varNames_ be the VarDeclaredNames of _body_.
          1. Let _varDeclarations_ be the VarScopedDeclarations of _body_.
          1. If _strict_ is *false*, then
            1. If _varEnv_ is a global Environment Record, then
              1. For each element _name_ of _varNames_, do
                1. If _varEnv_.HasLexicalDeclaration(_name_) is *true*, throw a *SyntaxError* exception.
                1. NOTE: `eval` will not create a global var declaration that would be shadowed by a global lexical declaration.
            1. Let _thisEnv_ be _lexEnv_.
            1. Assert: The following loop will terminate.
            1. Repeat, while _thisEnv_ is not the same as _varEnv_,
              1. If _thisEnv_ is not an object Environment Record, then
                1. NOTE: The environment of with statements cannot contain any lexical declaration so it doesn't need to be checked for var/let hoisting conflicts.
                1. For each element _name_ of _varNames_, do
                  1. If _thisEnv_.HasBinding(_name_) is *true*, then
                    1. [id="step-evaldeclarationinstantiation-throw-duplicate-binding"] Throw a *SyntaxError* exception.
                    1. NOTE: Annex <emu-xref href="#sec-variablestatements-in-catch-blocks"></emu-xref> defines alternate semantics for the above step.
                  1. NOTE: A direct eval will not hoist var declaration over a like-named lexical declaration.
              1. Set _thisEnv_ to _thisEnv_.[[OuterEnv]].
          1. Let _functionsToInitialize_ be a new empty List.
          1. Let _declaredFunctionNames_ be a new empty List.
          1. For each element _d_ of _varDeclarations_, in reverse List order, do
            1. If _d_ is neither a |VariableDeclaration| nor a |ForBinding| nor a |BindingIdentifier|, then
              1. Assert: _d_ is either a |FunctionDeclaration|, a |GeneratorDeclaration|, an |AsyncFunctionDeclaration|, or an |AsyncGeneratorDeclaration|.
              1. NOTE: If there are multiple function declarations for the same name, the last declaration is used.
              1. Let _fn_ be the sole element of the BoundNames of _d_.
              1. If _fn_ is not an element of _declaredFunctionNames_, then
                1. If _varEnv_ is a global Environment Record, then
                  1. Let _fnDefinable_ be ? _varEnv_.CanDeclareGlobalFunction(_fn_).
                  1. If _fnDefinable_ is *false*, throw a *TypeError* exception.
                1. Append _fn_ to _declaredFunctionNames_.
                1. Insert _d_ as the first element of _functionsToInitialize_.
          1. [id="step-evaldeclarationinstantiation-web-compat-insertion-point"] NOTE: Annex <emu-xref href="#sec-web-compat-evaldeclarationinstantiation"></emu-xref> adds additional steps at this point.
          1. Let _declaredVarNames_ be a new empty List.
          1. For each element _d_ of _varDeclarations_, do
            1. If _d_ is a |VariableDeclaration|, a |ForBinding|, or a |BindingIdentifier|, then
              1. For each String _vn_ of the BoundNames of _d_, do
                1. If _vn_ is not an element of _declaredFunctionNames_, then
                  1. If _varEnv_ is a global Environment Record, then
                    1. Let _vnDefinable_ be ? _varEnv_.CanDeclareGlobalVar(_vn_).
                    1. If _vnDefinable_ is *false*, throw a *TypeError* exception.
                  1. If _vn_ is not an element of _declaredVarNames_, then
                    1. Append _vn_ to _declaredVarNames_.
          1. [id="step-evaldeclarationinstantiation-post-validation"] NOTE: No abnormal terminations occur after this algorithm step unless _varEnv_ is a global Environment Record and the global object is a Proxy exotic object.
          1. Let _lexDeclarations_ be the LexicallyScopedDeclarations of _body_.
          1. For each element _d_ of _lexDeclarations_, do
            1. NOTE: Lexically declared names are only instantiated here but not initialized.
            1. For each element _dn_ of the BoundNames of _d_, do
              1. If IsConstantDeclaration of _d_ is *true*, then
                1. Perform ? _lexEnv_.CreateImmutableBinding(_dn_, *true*).
              1. Else,
                1. Perform ? _lexEnv_.CreateMutableBinding(_dn_, *false*).
          1. For each Parse Node _f_ of _functionsToInitialize_, do
            1. Let _fn_ be the sole element of the BoundNames of _f_.
            1. Let _fo_ be InstantiateFunctionObject of _f_ with argument _lexEnv_.
            1. If _varEnv_ is a global Environment Record, then
              1. Perform ? _varEnv_.CreateGlobalFunctionBinding(_fn_, _fo_, *true*).
            1. Else,
              1. Let _bindingExists_ be _varEnv_.HasBinding(_fn_).
              1. If _bindingExists_ is *false*, then
                1. Let _status_ be ! _varEnv_.CreateMutableBinding(_fn_, *true*).
                1. Assert: _status_ is not an abrupt completion because of validation preceding step <emu-xref href="#step-evaldeclarationinstantiation-post-validation"></emu-xref>.
                1. Perform ! _varEnv_.InitializeBinding(_fn_, _fo_).
              1. Else,
                1. Perform ! _varEnv_.SetMutableBinding(_fn_, _fo_, *false*).
          1. For each String _vn_ of _declaredVarNames_, do
            1. If _varEnv_ is a global Environment Record, then
              1. Perform ? _varEnv_.CreateGlobalVarBinding(_vn_, *true*).
            1. Else,
              1. Let _bindingExists_ be _varEnv_.HasBinding(_vn_).
              1. If _bindingExists_ is *false*, then
                1. Let _status_ be ! _varEnv_.CreateMutableBinding(_vn_, *true*).
                1. Assert: _status_ is not an abrupt completion because of validation preceding step <emu-xref href="#step-evaldeclarationinstantiation-post-validation"></emu-xref>.
                1. Perform ! _varEnv_.InitializeBinding(_vn_, *undefined*).
          1. Return NormalCompletion(~empty~).