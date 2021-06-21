          1. If _completion_ is an abrupt completion, perform ! Call(_promiseCapability_.[[Reject]], *undefined*, « _completion_.[[Value]] »).
          1. Else,
            1. Assert: _completion_ is a normal completion and _completion_.[[Value]] is *undefined*.
            1. Let _moduleRecord_ be ! HostResolveImportedModule(_referencingScriptOrModule_, _specifier_).
            1. Assert: Evaluate has already been invoked on _moduleRecord_ and successfully completed.
            1. Let _namespace_ be GetModuleNamespace(_moduleRecord_).
            1. If _namespace_ is an abrupt completion, perform ! Call(_promiseCapability_.[[Reject]], *undefined*, « _namespace_.[[Value]] »).
            1. Else, perform ! Call(_promiseCapability_.[[Resolve]], *undefined*, « _namespace_.[[Value]] »).