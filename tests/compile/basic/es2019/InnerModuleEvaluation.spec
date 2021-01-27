              1. If _module_ is not a Cyclic Module Record, then
                1. Perform ? _module_.Evaluate().
                1. Return _index_.
              1. If _module_.[[Status]] is `"evaluated"`, then
                1. If _module_.[[EvaluationError]] is *undefined*, return _index_.
                1. Otherwise return _module_.[[EvaluationError]].
              1. If _module_.[[Status]] is `"evaluating"`, return _index_.
              1. Assert: _module_.[[Status]] is `"instantiated"`.
              1. Set _module_.[[Status]] to `"evaluating"`.
              1. Set _module_.[[DFSIndex]] to _index_.
              1. Set _module_.[[DFSAncestorIndex]] to _index_.
              1. Increase _index_ by 1.
              1. Append _module_ to _stack_.
              1. For each String _required_ that is an element of _module_.[[RequestedModules]], do
                1. Let _requiredModule_ be ! HostResolveImportedModule(_module_, _required_).
                1. NOTE: Instantiate must be completed successfully prior to invoking this method, so every requested module is guaranteed to resolve successfully.
                1. Set _index_ to ? InnerModuleEvaluation(_requiredModule_, _stack_, _index_).
                1. Assert: _requiredModule_.[[Status]] is either `"evaluating"` or `"evaluated"`.
                1. Assert: _requiredModule_.[[Status]] is `"evaluating"` if and only if _requiredModule_ is in _stack_.
                1. If _requiredModule_.[[Status]] is `"evaluating"`, then
                  1. Assert: _requiredModule_ is a Cyclic Module Record.
                  1. Set _module_.[[DFSAncestorIndex]] to min(_module_.[[DFSAncestorIndex]], _requiredModule_.[[DFSAncestorIndex]]).
              1. Perform ? _module_.ExecuteModule().
              1. Assert: _module_ occurs exactly once in _stack_.
              1. Assert: _module_.[[DFSAncestorIndex]] is less than or equal to _module_.[[DFSIndex]].
              1. If _module_.[[DFSAncestorIndex]] equals _module_.[[DFSIndex]], then
                1. Let _done_ be *false*.
                1. Repeat, while _done_ is *false*,
                  1. Let _requiredModule_ be the last element in _stack_.
                  1. Remove the last element of _stack_.
                  1. Set _requiredModule_.[[Status]] to `"evaluated"`.
                  1. If _requiredModule_ and _module_ are the same Module Record, set _done_ to *true*.
              1. Return _index_.