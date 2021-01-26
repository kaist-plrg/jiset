            1. Let _module_ be this Source Text Module Record.
            1. For each ExportEntry Record _e_ in _module_.[[IndirectExportEntries]], do
              1. Let _resolution_ be ? _module_.ResolveExport(_e_.[[ExportName]]).
              1. If _resolution_ is *null* or *"ambiguous"*, throw a *SyntaxError* exception.
              1. Assert: _resolution_ is a ResolvedBinding Record.
            1. Assert: All named exports from _module_ are resolvable.
            1. Let _realm_ be _module_.[[Realm]].
            1. Assert: _realm_ is not *undefined*.
            1. Let _env_ be NewModuleEnvironment(_realm_.[[GlobalEnv]]).
            1. Set _module_.[[Environment]] to _env_.
            1. Let _envRec_ be _env_'s EnvironmentRecord.
            1. For each ImportEntry Record _in_ in _module_.[[ImportEntries]], do
              1. Let _importedModule_ be ! HostResolveImportedModule(_module_, _in_.[[ModuleRequest]]).
              1. NOTE: The above call cannot fail because imported module requests are a subset of _module_.[[RequestedModules]], and these have been resolved earlier in this algorithm.
              1. If _in_.[[ImportName]] is *"\*"*, then
                1. Let _namespace_ be ? GetModuleNamespace(_importedModule_).
                1. Perform ! _envRec_.CreateImmutableBinding(_in_.[[LocalName]], *true*).
                1. Call _envRec_.InitializeBinding(_in_.[[LocalName]], _namespace_).
              1. Else,
                1. Let _resolution_ be ? _importedModule_.ResolveExport(_in_.[[ImportName]]).
                1. If _resolution_ is *null* or *"ambiguous"*, throw a *SyntaxError* exception.
                1. If _resolution_.[[BindingName]] is *"\*namespace\*"*, then
                  1. Let _namespace_ be ? GetModuleNamespace(_resolution_.[[Module]]).
                  1. Perform ! _envRec_.CreateImmutableBinding(_in_.[[LocalName]], *true*).
                  1. Call _envRec_.InitializeBinding(_in_.[[LocalName]], _namespace_).
                1. Else,
                  1. Call _envRec_.CreateImportBinding(_in_.[[LocalName]], _resolution_.[[Module]], _resolution_.[[BindingName]]).
            1. Let _moduleContext_ be a new ECMAScript code execution context.
            1. Set the Function of _moduleContext_ to *null*.
            1. Assert: _module_.[[Realm]] is not *undefined*.
            1. Set the Realm of _moduleContext_ to _module_.[[Realm]].
            1. Set the ScriptOrModule of _moduleContext_ to _module_.
            1. Set the VariableEnvironment of _moduleContext_ to _module_.[[Environment]].
            1. Set the LexicalEnvironment of _moduleContext_ to _module_.[[Environment]].
            1. Set _module_.[[Context]] to _moduleContext_.
            1. Push _moduleContext_ onto the execution context stack; _moduleContext_ is now the running execution context.
            1. Let _code_ be _module_.[[ECMAScriptCode]].
            1. Let _varDeclarations_ be the VarScopedDeclarations of _code_.
            1. Let _declaredVarNames_ be a new empty List.
            1. For each element _d_ in _varDeclarations_, do
              1. For each element _dn_ of the BoundNames of _d_, do
                1. If _dn_ is not an element of _declaredVarNames_, then
                  1. Perform ! _envRec_.CreateMutableBinding(_dn_, *false*).
                  1. Call _envRec_.InitializeBinding(_dn_, *undefined*).
                  1. Append _dn_ to _declaredVarNames_.
            1. Let _lexDeclarations_ be the LexicallyScopedDeclarations of _code_.
            1. For each element _d_ in _lexDeclarations_, do
              1. For each element _dn_ of the BoundNames of _d_, do
                1. If IsConstantDeclaration of _d_ is *true*, then
                  1. Perform ! _envRec_.CreateImmutableBinding(_dn_, *true*).
                1. Else,
                  1. Perform ! _envRec_.CreateMutableBinding(_dn_, *false*).
                1. If _d_ is a |FunctionDeclaration|, a |GeneratorDeclaration|, an |AsyncFunctionDeclaration|, or an |AsyncGeneratorDeclaration|, then
                  1. Let _fo_ be InstantiateFunctionObject of _d_ with argument _env_.
                  1. Call _envRec_.InitializeBinding(_dn_, _fo_).
            1. Remove _moduleContext_ from the execution context stack.
            1. Return NormalCompletion(~empty~).