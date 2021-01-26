            1. Let _module_ be this Source Text Module Record.
            1. Let _realm_ be _module_.[[Realm]].
            1. Assert: _realm_ is not *undefined*.
            1. Let _code_ be _module_.[[ECMAScriptCode]].
            1. If _module_.[[Environment]] is not *undefined*, return NormalCompletion(~empty~).
            1. Let _env_ be NewModuleEnvironment(_realm_.[[GlobalEnv]]).
            1. Set _module_.[[Environment]] to _env_.
            1. For each String _required_ that is an element of _module_.[[RequestedModules]] do,
              1. NOTE: Before instantiating a module, all of the modules it requested must be available. An implementation may perform this test at any time prior to this point.
              1. Let _requiredModule_ be ? HostResolveImportedModule(_module_, _required_).
              1. Perform ? _requiredModule_.ModuleDeclarationInstantiation().
            1. For each ExportEntry Record _e_ in _module_.[[IndirectExportEntries]], do
              1. Let _resolution_ be ? _module_.ResolveExport(_e_.[[ExportName]], « », « »).
              1. If _resolution_ is *null* or _resolution_ is `"ambiguous"`, throw a *SyntaxError* exception.
            1. Assert: all named exports from _module_ are resolvable.
            1. Let _envRec_ be _env_'s EnvironmentRecord.
            1. For each ImportEntry Record _in_ in _module_.[[ImportEntries]], do
              1. Let _importedModule_ be ? HostResolveImportedModule(_module_, _in_.[[ModuleRequest]]).
              1. If _in_.[[ImportName]] is `"*"`, then
                1. Let _namespace_ be ? GetModuleNamespace(_importedModule_).
                1. Perform ! _envRec_.CreateImmutableBinding(_in_.[[LocalName]], *true*).
                1. Call _envRec_.InitializeBinding(_in_.[[LocalName]], _namespace_).
              1. Else,
                1. Let _resolution_ be ? _importedModule_.ResolveExport(_in_.[[ImportName]], « », « »).
                1. If _resolution_ is *null* or _resolution_ is `"ambiguous"`, throw a *SyntaxError* exception.
                1. Call _envRec_.CreateImportBinding(_in_.[[LocalName]], _resolution_.[[Module]], _resolution_.[[BindingName]]).
            1. Let _varDeclarations_ be the VarScopedDeclarations of _code_.
            1. Let _declaredVarNames_ be a new empty List.
            1. For each element _d_ in _varDeclarations_ do
              1. For each element _dn_ of the BoundNames of _d_ do
                1. If _dn_ is not an element of _declaredVarNames_, then
                  1. Perform ! _envRec_.CreateMutableBinding(_dn_, *false*).
                  1. Call _envRec_.InitializeBinding(_dn_, *undefined*).
                  1. Append _dn_ to _declaredVarNames_.
            1. Let _lexDeclarations_ be the LexicallyScopedDeclarations of _code_.
            1. For each element _d_ in _lexDeclarations_ do
              1. For each element _dn_ of the BoundNames of _d_ do
                1. If IsConstantDeclaration of _d_ is *true*, then
                  1. Perform ! _envRec_.CreateImmutableBinding(_dn_, *true*).
                1. Else,
                  1. Perform ! _envRec_.CreateMutableBinding(_dn_, *false*).
                1. If _d_ is a |GeneratorDeclaration| production or a |FunctionDeclaration| production, then
                  1. Let _fo_ be the result of performing InstantiateFunctionObject for _d_ with argument _env_.
                  1. Call _envRec_.InitializeBinding(_dn_, _fo_).
            1. Return NormalCompletion(~empty~).