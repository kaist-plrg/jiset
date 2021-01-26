        1. Let _calleeContext_ be the running execution context.
        1. Let _code_ be _func_.[[ECMAScriptCode]].
        1. Let _strict_ be _func_.[[Strict]].
        1. Let _formals_ be _func_.[[FormalParameters]].
        1. Let _parameterNames_ be the BoundNames of _formals_.
        1. If _parameterNames_ has any duplicate entries, let _hasDuplicates_ be *true*. Otherwise, let _hasDuplicates_ be *false*.
        1. Let _simpleParameterList_ be IsSimpleParameterList of _formals_.
        1. Let _hasParameterExpressions_ be ContainsExpression of _formals_.
        1. Let _varNames_ be the VarDeclaredNames of _code_.
        1. Let _varDeclarations_ be the VarScopedDeclarations of _code_.
        1. Let _lexicalNames_ be the LexicallyDeclaredNames of _code_.
        1. Let _functionNames_ be a new empty List.
        1. Let _functionsToInitialize_ be a new empty List.
        1. For each _d_ in _varDeclarations_, in reverse list order, do
          1. If _d_ is neither a |VariableDeclaration| nor a |ForBinding| nor a |BindingIdentifier|, then
            1. Assert: _d_ is either a |FunctionDeclaration|, a |GeneratorDeclaration|, an |AsyncFunctionDeclaration|, or an |AsyncGeneratorDeclaration|.
            1. Let _fn_ be the sole element of the BoundNames of _d_.
            1. If _fn_ is not an element of _functionNames_, then
              1. Insert _fn_ as the first element of _functionNames_.
              1. NOTE: If there are multiple function declarations for the same name, the last declaration is used.
              1. Insert _d_ as the first element of _functionsToInitialize_.
        1. Let _argumentsObjectNeeded_ be *true*.
        1. If _func_.[[ThisMode]] is ~lexical~, then
          1. NOTE: Arrow functions never have an arguments objects.
          1. Set _argumentsObjectNeeded_ to *false*.
        1. Else if *"arguments"* is an element of _parameterNames_, then
          1. Set _argumentsObjectNeeded_ to *false*.
        1. Else if _hasParameterExpressions_ is *false*, then
          1. If *"arguments"* is an element of _functionNames_ or if *"arguments"* is an element of _lexicalNames_, then
            1. Set _argumentsObjectNeeded_ to *false*.
        1. If _strict_ is *true* or if _hasParameterExpressions_ is *false*, then
          1. NOTE: Only a single lexical environment is needed for the parameters and top-level vars.
          1. Let _env_ be the LexicalEnvironment of _calleeContext_.
          1. Let _envRec_ be _env_'s EnvironmentRecord.
        1. Else,
          1. NOTE: A separate Environment Record is needed to ensure that bindings created by direct eval calls in the formal parameter list are outside the environment where parameters are declared.
          1. Let _calleeEnv_ be the LexicalEnvironment of _calleeContext_.
          1. Let _env_ be NewDeclarativeEnvironment(_calleeEnv_).
          1. Let _envRec_ be _env_'s EnvironmentRecord.
          1. Assert: The VariableEnvironment of _calleeContext_ is _calleeEnv_.
          1. Set the LexicalEnvironment of _calleeContext_ to _env_.
        1. For each String _paramName_ in _parameterNames_, do
          1. Let _alreadyDeclared_ be _envRec_.HasBinding(_paramName_).
          1. NOTE: Early errors ensure that duplicate parameter names can only occur in non-strict functions that do not have parameter default values or rest parameters.
          1. If _alreadyDeclared_ is *false*, then
            1. Perform ! _envRec_.CreateMutableBinding(_paramName_, *false*).
            1. If _hasDuplicates_ is *true*, then
              1. Perform ! _envRec_.InitializeBinding(_paramName_, *undefined*).
        1. If _argumentsObjectNeeded_ is *true*, then
          1. If _strict_ is *true* or if _simpleParameterList_ is *false*, then
            1. Let _ao_ be CreateUnmappedArgumentsObject(_argumentsList_).
          1. Else,
            1. NOTE: A mapped argument object is only provided for non-strict functions that don't have a rest parameter, any parameter default value initializers, or any destructured parameters.
            1. Let _ao_ be CreateMappedArgumentsObject(_func_, _formals_, _argumentsList_, _envRec_).
          1. If _strict_ is *true*, then
            1. Perform ! _envRec_.CreateImmutableBinding(*"arguments"*, *false*).
          1. Else,
            1. Perform ! _envRec_.CreateMutableBinding(*"arguments"*, *false*).
          1. Call _envRec_.InitializeBinding(*"arguments"*, _ao_).
          1. Let _parameterBindings_ be a new List of _parameterNames_ with *"arguments"* appended.
        1. Else,
          1. Let _parameterBindings_ be _parameterNames_.
        1. Let _iteratorRecord_ be CreateListIteratorRecord(_argumentsList_).
        1. If _hasDuplicates_ is *true*, then
          1. Perform ? IteratorBindingInitialization for _formals_ with _iteratorRecord_ and *undefined* as arguments.
        1. Else,
          1. Perform ? IteratorBindingInitialization for _formals_ with _iteratorRecord_ and _env_ as arguments.
        1. If _hasParameterExpressions_ is *false*, then
          1. NOTE: Only a single lexical environment is needed for the parameters and top-level vars.
          1. Let _instantiatedVarNames_ be a copy of the List _parameterBindings_.
          1. For each _n_ in _varNames_, do
            1. If _n_ is not an element of _instantiatedVarNames_, then
              1. Append _n_ to _instantiatedVarNames_.
              1. Perform ! _envRec_.CreateMutableBinding(_n_, *false*).
              1. Call _envRec_.InitializeBinding(_n_, *undefined*).
          1. Let _varEnv_ be _env_.
          1. Let _varEnvRec_ be _envRec_.
        1. Else,
          1. NOTE: A separate Environment Record is needed to ensure that closures created by expressions in the formal parameter list do not have visibility of declarations in the function body.
          1. Let _varEnv_ be NewDeclarativeEnvironment(_env_).
          1. Let _varEnvRec_ be _varEnv_'s EnvironmentRecord.
          1. Set the VariableEnvironment of _calleeContext_ to _varEnv_.
          1. Let _instantiatedVarNames_ be a new empty List.
          1. For each _n_ in _varNames_, do
            1. If _n_ is not an element of _instantiatedVarNames_, then
              1. Append _n_ to _instantiatedVarNames_.
              1. Perform ! _varEnvRec_.CreateMutableBinding(_n_, *false*).
              1. If _n_ is not an element of _parameterBindings_ or if _n_ is an element of _functionNames_, let _initialValue_ be *undefined*.
              1. Else,
                1. Let _initialValue_ be ! _envRec_.GetBindingValue(_n_, *false*).
              1. Call _varEnvRec_.InitializeBinding(_n_, _initialValue_).
              1. NOTE: A var with the same name as a formal parameter initially has the same value as the corresponding initialized parameter.
        1. NOTE: Annex <emu-xref href="#sec-web-compat-functiondeclarationinstantiation"></emu-xref> adds additional steps at this point.
        1. If _strict_ is *false*, then
          1. Let _lexEnv_ be NewDeclarativeEnvironment(_varEnv_).
          1. NOTE: Non-strict functions use a separate lexical Environment Record for top-level lexical declarations so that a direct eval can determine whether any var scoped declarations introduced by the eval code conflict with pre-existing top-level lexically scoped declarations. This is not needed for strict functions because a strict direct eval always places all declarations into a new Environment Record.
        1. Else, let _lexEnv_ be _varEnv_.
        1. Let _lexEnvRec_ be _lexEnv_'s EnvironmentRecord.
        1. Set the LexicalEnvironment of _calleeContext_ to _lexEnv_.
        1. Let _lexDeclarations_ be the LexicallyScopedDeclarations of _code_.
        1. For each element _d_ in _lexDeclarations_, do
          1. NOTE: A lexically declared name cannot be the same as a function/generator declaration, formal parameter, or a var name. Lexically declared names are only instantiated here but not initialized.
          1. For each element _dn_ of the BoundNames of _d_, do
            1. If IsConstantDeclaration of _d_ is *true*, then
              1. Perform ! _lexEnvRec_.CreateImmutableBinding(_dn_, *true*).
            1. Else,
              1. Perform ! _lexEnvRec_.CreateMutableBinding(_dn_, *false*).
        1. For each Parse Node _f_ in _functionsToInitialize_, do
          1. Let _fn_ be the sole element of the BoundNames of _f_.
          1. Let _fo_ be InstantiateFunctionObject of _f_ with argument _lexEnv_.
          1. Perform ! _varEnvRec_.SetMutableBinding(_fn_, _fo_, *false*).
        1. Return NormalCompletion(~empty~).