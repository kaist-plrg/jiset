          1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
          1. Let _loopEnv_ be NewDeclarativeEnvironment(_oldEnv_).
          1. Let _loopEnvRec_ be _loopEnv_'s EnvironmentRecord.
          1. Let _isConst_ be the result of performing IsConstantDeclaration of |LexicalDeclaration|.
          1. Let _boundNames_ be the BoundNames of |LexicalDeclaration|.
          1. For each element _dn_ of _boundNames_ do
            1. If _isConst_ is *true*, then
              1. Perform ! _loopEnvRec_.CreateImmutableBinding(_dn_, *true*).
            1. Else,
              1. Perform ! _loopEnvRec_.CreateMutableBinding(_dn_, *false*).
          1. Set the running execution context's LexicalEnvironment to _loopEnv_.
          1. Let _forDcl_ be the result of evaluating |LexicalDeclaration|.
          1. If _forDcl_ is an abrupt completion, then
            1. Set the running execution context's LexicalEnvironment to _oldEnv_.
            1. Return Completion(_forDcl_).
          1. If _isConst_ is *false*, let _perIterationLets_ be _boundNames_; otherwise let _perIterationLets_ be « ».
          1. Let _bodyResult_ be ForBodyEvaluation(the first |Expression|, the second |Expression|, |Statement|, _perIterationLets_, _labelSet_).
          1. Set the running execution context's LexicalEnvironment to _oldEnv_.
          1. Return Completion(_bodyResult_).