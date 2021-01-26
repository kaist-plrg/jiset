            1. Let _module_ be this Source Text Module Record.
            1. Assert: ModuleDeclarationInstantiation has already been invoked on _module_ and successfully completed.
            1. Assert: _module_.[[Realm]] is not *undefined*.
            1. If _module_.[[Evaluated]] is *true*, return *undefined*.
            1. Set _module_.[[Evaluated]] to *true*.
            1. For each String _required_ that is an element of _module_.[[RequestedModules]] do,
              1. Let _requiredModule_ be ? HostResolveImportedModule(_module_, _required_).
              1. Perform ? _requiredModule_.ModuleEvaluation().
            1. Let _moduleCxt_ be a new ECMAScript code execution context.
            1. Set the Function of _moduleCxt_ to *null*.
            1. Set the Realm of _moduleCxt_ to _module_.[[Realm]].
            1. Set the ScriptOrModule of _moduleCxt_ to _module_.
            1. Assert: _module_ has been linked and declarations in its module environment have been instantiated.
            1. Set the VariableEnvironment of _moduleCxt_ to _module_.[[Environment]].
            1. Set the LexicalEnvironment of _moduleCxt_ to _module_.[[Environment]].
            1. Suspend the currently running execution context.
            1. Push _moduleCxt_ on to the execution context stack; _moduleCxt_ is now the running execution context.
            1. Let _result_ be the result of evaluating _module_.[[ECMAScriptCode]].
            1. Suspend _moduleCxt_ and remove it from the execution context stack.
            1. Resume the context that is now on the top of the execution context stack as the running execution context.
            1. Return Completion(_result_).